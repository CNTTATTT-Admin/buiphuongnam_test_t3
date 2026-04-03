package vn.kurisu.mentormatch.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.MenteeProfileResponse;
import vn.kurisu.mentormatch.dto.response.RoleResponse;
import vn.kurisu.mentormatch.dto.response.UserResponse;
import vn.kurisu.mentormatch.entity.Follow;
import vn.kurisu.mentormatch.entity.User;
import vn.kurisu.mentormatch.exception.AppException;
import vn.kurisu.mentormatch.exception.ErrorCode;
import vn.kurisu.mentormatch.repository.FollowRepository;
import vn.kurisu.mentormatch.repository.UserRepository;
import vn.kurisu.mentormatch.service.FollowService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        String userName = authentication.getName();
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    @Override
    public ApiResponse<String> toggleFollow(Integer targetUserId) {
        User currentUser = getAuthenticatedUser();

        if (currentUser.getId().equals(targetUserId)) {
            throw new RuntimeException("You cannot follow yourself");
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Optional<Follow> existingFollow = followRepository.findByFollowerIdAndFollowingId(currentUser.getId(), targetUser.getId());

        if (existingFollow.isPresent()) {
            followRepository.delete(existingFollow.get());
            return ApiResponse.<String>builder().result("Unfollowed successfully").build();
        } else {
            Follow newFollow = Follow.builder()
                    .follower(currentUser)
                    .following(targetUser)
                    .build();
            followRepository.save(newFollow);
            return ApiResponse.<String>builder().result("Followed successfully").build();
        }
    }

    @Override
    public ApiResponse<List<UserResponse>> getFollowing() {
        User currentUser = getAuthenticatedUser();
        List<Follow> follows = followRepository.findByFollowerIdWithFollowing(currentUser.getId());
        
        List<UserResponse> followingList = follows.stream()
                .map(follow -> toUserResponse(follow.getFollowing()))
                .collect(Collectors.toList());

        return ApiResponse.<List<UserResponse>>builder().result(followingList).build();
    }

    @Override
    public ApiResponse<List<UserResponse>> getFollowers(Integer targetUserId) {
        List<Follow> follows = followRepository.findByFollowingIdWithFollower(targetUserId);
        
        List<UserResponse> followersList = follows.stream()
                .map(follow -> toUserResponse(follow.getFollower()))
                .collect(Collectors.toList());

        return ApiResponse.<List<UserResponse>>builder().result(followersList).build();
    }

    @Override
    public ApiResponse<Boolean> checkFollowStatus(Integer targetUserId) {
        User currentUser = getAuthenticatedUser();
        boolean isFollowing = followRepository.existsByFollowerIdAndFollowingId(currentUser.getId(), targetUserId);
        
        return ApiResponse.<Boolean>builder().result(isFollowing).build();
    }

    private UserResponse toUserResponse(User user) {
        Set<RoleResponse> roleResponses = user.getRoles().stream()
                .map(role -> RoleResponse.builder()
                        .id(role.getId())
                        .name(role.getName())
                        .build())
                .collect(Collectors.toSet());

        return UserResponse.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .email(user.getEmail())
                .phone(user.getPhone())
                .isActive(user.getIsActive())
                .roles(roleResponses)
                .build();
    }
}
