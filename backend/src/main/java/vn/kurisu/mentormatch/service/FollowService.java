package vn.kurisu.mentormatch.service;

import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.UserResponse;

import java.util.List;

public interface FollowService {
    ApiResponse<String> toggleFollow(Integer targetUserId);
    ApiResponse<List<UserResponse>> getFollowing();
    ApiResponse<List<UserResponse>> getFollowers(Integer targetUserId);
    ApiResponse<Boolean> checkFollowStatus(Integer targetUserId);
}
