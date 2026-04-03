package vn.kurisu.mentormatch.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.kurisu.mentormatch.dto.request.UserCreationRequest;
import vn.kurisu.mentormatch.dto.request.UserUpdateRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.MenteeProfileResponse;
import vn.kurisu.mentormatch.dto.response.RoleResponse;
import vn.kurisu.mentormatch.dto.response.UserResponse;
import vn.kurisu.mentormatch.entity.Role;
import vn.kurisu.mentormatch.entity.User;
import vn.kurisu.mentormatch.exception.AppException;
import vn.kurisu.mentormatch.exception.ErrorCode;
import vn.kurisu.mentormatch.repository.RoleRepository;
import vn.kurisu.mentormatch.repository.UserRepository;
import vn.kurisu.mentormatch.repository.ReviewRepository;
import vn.kurisu.mentormatch.service.UserService;
import vn.kurisu.mentormatch.dto.response.MentorProfileResponse;
import vn.kurisu.mentormatch.service.CloudinaryService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;
    private final ReviewRepository reviewRepository;

    @Override
    public ApiResponse<UserResponse> create(UserCreationRequest request) {
        if (userRepository.existsByUserName(request.getUserName())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = User.builder()
                .userName(request.getUserName())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        Set<Role> roles = new HashSet<>();
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            for (String roleName : request.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
                roles.add(role);
            }
        } else {
            Role defaultRole = roleRepository.findByName("ROLE_MENTEE")
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
            roles.add(defaultRole);
        }
        user.setRoles(roles);

        user = userRepository.save(user);
        return ApiResponse.<UserResponse>builder().result(toUserResponse(user)).build();
    }

    @Override
    public ApiResponse<List<UserResponse>> getAll() {
        List<UserResponse> users = userRepository.findAll().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());

        return ApiResponse.<List<UserResponse>>builder().result(users).build();
    }

    @Override
    public ApiResponse<List<UserResponse>> getMentors() {
        List<UserResponse> mentors = userRepository.findByRolesName("ROLE_MENTOR").stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());

        return ApiResponse.<List<UserResponse>>builder().result(mentors).build();
    }

    @Override
    public ApiResponse<UserResponse> getById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return ApiResponse.<UserResponse>builder().result(toUserResponse(user)).build();
    }

    @Override
    public ApiResponse<UserResponse> update(Integer id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        if (request.getIsActive() != null) {
            user.setIsActive(request.getIsActive());
        }

        if (request.getRoles() != null) {
            Set<Role> roles = new HashSet<>();
            for (String roleName : request.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        user = userRepository.save(user);

        return ApiResponse.<UserResponse>builder().result(toUserResponse(user)).build();
    }

    @Override
    public ApiResponse<Void> delete(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        userRepository.delete(user);

        return ApiResponse.<Void>builder().message("User has been deleted").build();
    }

    @Override
    public ApiResponse<String> updateAvatar(org.springframework.web.multipart.MultipartFile file) {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userName = authentication.getName();
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (user.getAvatarUrl() != null && user.getAvatarUrl().contains("cloudinary.com")) {
            cloudinaryService.deleteImage(user.getAvatarUrl());
        }

        String url = cloudinaryService.uploadImage(file);
        user.setAvatarUrl(url);
        userRepository.save(user);

        return ApiResponse.<String>builder()
                .message("Avatar updated successfully")
                .result(url)
                .build();
    }

    private UserResponse toUserResponse(User user) {
        Set<RoleResponse> roleResponses = user.getRoles().stream()
                .map(role -> RoleResponse.builder()
                        .id(role.getId())
                        .name(role.getName())
                        .build())
                .collect(Collectors.toSet());

        MenteeProfileResponse menteeProfileResponse = null;
        if (user.getMenteeProfile() != null) {
            menteeProfileResponse = MenteeProfileResponse.builder()
                    .id(user.getMenteeProfile().getId())
                    .currentEducation(user.getMenteeProfile().getCurrentEducation())
                    .learningGoals(user.getMenteeProfile().getLearningGoals())
                    .interests(user.getMenteeProfile().getInterests())
                    .build();
        }

        MentorProfileResponse mentorProfileResponse = null;
        if (user.getMentorProfile() != null) {
            Double rating = reviewRepository.getAverageRatingByMentorId(user.getId());
            Integer reviewCount = reviewRepository.findByMentorIdOrderByCreatedAtDesc(user.getId()).size();

            mentorProfileResponse = MentorProfileResponse.builder()
                    .id(user.getMentorProfile().getId())
                    .title(user.getMentorProfile().getTitle())
                    .bio(user.getMentorProfile().getBio())
                    .yearsOfExperience(user.getMentorProfile().getYearsOfExperience())
                    .isVerified(user.getMentorProfile().getIsVerified())
                    .walletBalance(user.getMentorProfile().getWalletBalance())
                    .skills(user.getMentorProfile().getSkills() != null ? 
                            user.getMentorProfile().getSkills().stream().map(s -> s.getName()).collect(Collectors.toList()) : null)
                    .rating(rating)
                    .reviewCount(reviewCount)
                    .build();
        }

        return UserResponse.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .email(user.getEmail())
                .phone(user.getPhone())
                .isActive(user.getIsActive())
                .roles(roleResponses)
                .menteeProfile(menteeProfileResponse)
                .mentorProfile(mentorProfileResponse)
                .build();
    }
}
