package vn.kurisu.mentormatch.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.MentorRequestResponse;
import vn.kurisu.mentormatch.entity.*;
import vn.kurisu.mentormatch.exception.AppException;
import vn.kurisu.mentormatch.exception.ErrorCode;
import vn.kurisu.mentormatch.repository.MentorProfileRepository;
import vn.kurisu.mentormatch.repository.RoleRepository;
import vn.kurisu.mentormatch.repository.SkillRepository;
import vn.kurisu.mentormatch.repository.UserRepository;
import vn.kurisu.mentormatch.service.CloudinaryService;
import vn.kurisu.mentormatch.service.MentorRegistrationService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MentorRegistrationServiceImpl implements MentorRegistrationService {

    private final MentorProfileRepository mentorProfileRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SkillRepository skillRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional
    public ApiResponse<String> submitRegistration(Integer userId, String bio, List<Integer> skillIds, List<MultipartFile> certificateFiles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (mentorProfileRepository.findByUserId(userId).isPresent()) {
            MentorProfile existing = mentorProfileRepository.findByUserId(userId).get();
            if (existing.getIsVerified()) {
                return ApiResponse.<String>builder().code(4000).message("Bạn đã là Mentor rồi!").build();
            }
            return ApiResponse.<String>builder().code(4000).message("Hồ sơ của bạn đã được gửi. Vui lòng chờ admin duyệt.").build();
        }

        MentorProfile profile = MentorProfile.builder()
                .user(user)
                .title("Ứng viên Mentor") // Temporary title
                .bio(bio)
                .isVerified(false)
                .build();

        if (skillIds != null && !skillIds.isEmpty()) {
            List<Skill> skills = skillRepository.findAllById(skillIds);
            profile.getSkills().addAll(skills);
        }

        if (certificateFiles != null && !certificateFiles.isEmpty()) {
            for (MultipartFile file : certificateFiles) {
                try {
                    String url = cloudinaryService.uploadImage(file);
                    Certificate cert = Certificate.builder()
                            .name(file.getOriginalFilename() != null ? file.getOriginalFilename() : "Certificate")
                            .fileUrl(url)
                            .isApproved(false)
                            .mentorProfile(profile)
                            .build();
                    profile.getCertificates().add(cert);
                } catch (Exception e) {
                    log.error("Failed to upload certificate image", e);
                }
            }
        }

        mentorProfileRepository.save(profile);
        return ApiResponse.<String>builder()
                .code(1000)
                .result("Gửi hồ sơ đăng ký thành công. Vui lòng đợi quản trị viên duyệt.")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<MentorRequestResponse>> getPendingRequests() {
        List<MentorProfile> profiles = mentorProfileRepository.findByIsVerifiedFalse();
        List<MentorRequestResponse> responses = profiles.stream().map(this::mapToResponse).toList();
        return ApiResponse.<List<MentorRequestResponse>>builder()
                .code(1000)
                .result(responses)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<String> approveRequest(Integer profileId) {
        MentorProfile profile = mentorProfileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        if (profile.getIsVerified()) {
            return ApiResponse.<String>builder().code(4000).message("Hồ sơ này đã được duyệt").build();
        }

        profile.setIsVerified(true);
        profile.setTitle("Mentor");

        User user = profile.getUser();
        Role mentorRole = roleRepository.findByName("ROLE_MENTOR")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        user.getRoles().add(mentorRole);

        mentorProfileRepository.save(profile);
        userRepository.save(user);

        return ApiResponse.<String>builder()
                .code(1000)
                .result("Đã phê duyệt thành công. Tài khoản đã được cấp quyền Mentor.")
                .build();
    }

    private MentorRequestResponse mapToResponse(MentorProfile profile) {
        return MentorRequestResponse.builder()
                .profileId(profile.getId())
                .userId(profile.getUser().getId())
                .fullName(profile.getUser().getFullName())
                .email(profile.getUser().getEmail())
                .phone(profile.getUser().getPhone())
                .avatarUrl(profile.getUser().getAvatarUrl())
                .bio(profile.getBio())
                .yearsOfExperience(profile.getYearsOfExperience())
                .skills(profile.getSkills().stream().map(Skill::getName).toList())
                .certificates(profile.getCertificates().stream().map(c ->
                        MentorRequestResponse.CertificateDto.builder()
                                .id(c.getId())
                                .name(c.getName())
                                .fileUrl(c.getFileUrl())
                                .build()
                ).toList())
                .build();
    }
}
