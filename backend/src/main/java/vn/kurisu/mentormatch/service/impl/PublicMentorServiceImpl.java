package vn.kurisu.mentormatch.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.CertificateResponseDto;
import vn.kurisu.mentormatch.dto.response.MenteeProfileResponse;
import vn.kurisu.mentormatch.dto.response.MentorProfileResponse;
import vn.kurisu.mentormatch.dto.response.UserProfileResponse;
import vn.kurisu.mentormatch.entity.MenteeProfile;
import vn.kurisu.mentormatch.entity.MentorProfile;
import vn.kurisu.mentormatch.entity.Role;
import vn.kurisu.mentormatch.entity.Skill;
import vn.kurisu.mentormatch.entity.User;
import vn.kurisu.mentormatch.exception.AppException;
import vn.kurisu.mentormatch.exception.ErrorCode;
import vn.kurisu.mentormatch.repository.UserRepository;
import vn.kurisu.mentormatch.service.PublicMentorService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicMentorServiceImpl implements PublicMentorService {

    private final UserRepository userRepository;

    @Override
    public ApiResponse<List<UserProfileResponse>> getAllPublicMentors() {
        // Query users with ROLE_MENTOR, returning the mapped ProfileResponses
        List<UserProfileResponse> mentors = userRepository.findByRolesName("ROLE_MENTOR").stream()
                .map(this::mapToUserProfileResponse)
                .collect(Collectors.toList());

        return ApiResponse.<List<UserProfileResponse>>builder()
                .result(mentors)
                .build();
    }

    @Override
    public ApiResponse<UserProfileResponse> getPublicMentorProfile(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        boolean isMentor = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_MENTOR"));

        if (!isMentor) {
            throw new RuntimeException("User is not a mentor");
        }

        return ApiResponse.<UserProfileResponse>builder()
                .result(mapToUserProfileResponse(user))
                .build();
    }

    private UserProfileResponse mapToUserProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .phone(user.getPhone())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .mentorProfile(mapToMentorProfileResponse(user.getMentorProfile()))
                .menteeProfile(mapToMenteeProfileResponse(user.getMenteeProfile()))
                .build();
    }

    private MentorProfileResponse mapToMentorProfileResponse(MentorProfile profile) {
        if (profile == null) return null;
        return MentorProfileResponse.builder()
                .id(profile.getId())
                .title(profile.getTitle())
                .bio(profile.getBio())
                .yearsOfExperience(profile.getYearsOfExperience())
                .isVerified(profile.getIsVerified())
                .walletBalance(profile.getWalletBalance())
                .skills(profile.getSkills().stream().map(Skill::getName).collect(Collectors.toList()))
                .certificates(profile.getCertificates().stream().map(c -> 
                    CertificateResponseDto.builder()
                        .id(c.getId())
                        .name(c.getName())
                        .fileUrl(c.getFileUrl())
                        .isApproved(c.getIsApproved())
                        .build()
                ).collect(Collectors.toList()))
                .build();
    }

    private MenteeProfileResponse mapToMenteeProfileResponse(MenteeProfile profile) {
        if (profile == null) return null;
        return MenteeProfileResponse.builder()
                .id(profile.getId())
                .currentEducation(profile.getCurrentEducation())
                .learningGoals(profile.getLearningGoals())
                .interests(profile.getInterests())
                .build();
    }
}
