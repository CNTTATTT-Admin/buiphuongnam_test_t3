package vn.kurisu.mentormatch.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.kurisu.mentormatch.dto.request.MenteeProfileRequest;
import vn.kurisu.mentormatch.dto.request.MentorProfileRequest;
import vn.kurisu.mentormatch.dto.request.UserProfileRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.CertificateResponseDto;
import vn.kurisu.mentormatch.dto.response.MenteeProfileResponse;
import vn.kurisu.mentormatch.dto.response.MentorProfileResponse;
import vn.kurisu.mentormatch.dto.response.UserProfileResponse;
import vn.kurisu.mentormatch.entity.*;
import vn.kurisu.mentormatch.repository.CertificateRepository;
import vn.kurisu.mentormatch.repository.MenteeProfileRepository;
import vn.kurisu.mentormatch.repository.MentorProfileRepository;
import vn.kurisu.mentormatch.repository.SkillRepository;
import vn.kurisu.mentormatch.repository.UserRepository;
import vn.kurisu.mentormatch.service.ProfileService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final MentorProfileRepository mentorProfileRepository;
    private final MenteeProfileRepository menteeProfileRepository;
    private final SkillRepository skillRepository;
    private final CertificateRepository certificateRepository;

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public ApiResponse<UserProfileResponse> getMyProfile() {
        User currentUser = getCurrentUser();
        return ApiResponse.<UserProfileResponse>builder()
                .result(mapToUserProfileResponse(currentUser))
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<UserProfileResponse> updateBasicProfile(UserProfileRequest request) {
        User currentUser = getCurrentUser();
        
        currentUser.setFullName(request.getFullName());
        currentUser.setAvatarUrl(request.getAvatarUrl());
        currentUser.setPhone(request.getPhone());
        
        userRepository.save(currentUser);

        return ApiResponse.<UserProfileResponse>builder()
                .result(mapToUserProfileResponse(currentUser))
                .message("Basic profile updated successfully")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<UserProfileResponse> updateMentorProfile(MentorProfileRequest request) {
        User currentUser = getCurrentUser();
        
        MentorProfile mentorProfile = currentUser.getMentorProfile();
        if (mentorProfile == null) {
            mentorProfile = new MentorProfile();
            mentorProfile.setUser(currentUser);
        }

        mentorProfile.setTitle(request.getTitle());
        mentorProfile.setBio(request.getBio());
        mentorProfile.setYearsOfExperience(request.getYearsOfExperience());

        // Handle skills
        Set<Skill> skillSet = new HashSet<>();
        if (request.getSkills() != null) {
            for (String skillName : request.getSkills()) {
                Skill skill = skillRepository.findByName(skillName)
                    .orElseGet(() -> skillRepository.save(Skill.builder().name(skillName).build()));
                skillSet.add(skill);
            }
        }
        mentorProfile.setSkills(skillSet);

        // Handle certificates
        if (request.getCertificates() != null) {
            // Very basic replacement strategy for simplicity, in a real app you might want to sync/merge
            mentorProfile.getCertificates().clear();
            for (var certDto : request.getCertificates()) {
                Certificate cert = Certificate.builder()
                        .name(certDto.getName())
                        .fileUrl(certDto.getFileUrl())
                        .isApproved(false)
                        .mentorProfile(mentorProfile)
                        .build();
                mentorProfile.getCertificates().add(cert);
            }
        }

        mentorProfileRepository.save(mentorProfile);
        
        // Refresh currentUser to include the new/updated profile
        currentUser.setMentorProfile(mentorProfile);

        return ApiResponse.<UserProfileResponse>builder()
                .result(mapToUserProfileResponse(currentUser))
                .message("Mentor profile updated successfully")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<UserProfileResponse> updateMenteeProfile(MenteeProfileRequest request) {
        User currentUser = getCurrentUser();

        MenteeProfile menteeProfile = currentUser.getMenteeProfile();
        if (menteeProfile == null) {
            menteeProfile = new MenteeProfile();
            menteeProfile.setUser(currentUser);
        }

        menteeProfile.setCurrentEducation(request.getCurrentEducation());
        menteeProfile.setLearningGoals(request.getLearningGoals());
        menteeProfile.setInterests(request.getInterests());

        menteeProfileRepository.save(menteeProfile);
        
        // Refresh currentUser
        currentUser.setMenteeProfile(menteeProfile);

        return ApiResponse.<UserProfileResponse>builder()
                .result(mapToUserProfileResponse(currentUser))
                .message("Mentee profile updated successfully")
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
