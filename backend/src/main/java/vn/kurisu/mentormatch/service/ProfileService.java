package vn.kurisu.mentormatch.service;

import vn.kurisu.mentormatch.dto.request.MenteeProfileRequest;
import vn.kurisu.mentormatch.dto.request.MentorProfileRequest;
import vn.kurisu.mentormatch.dto.request.UserProfileRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.UserProfileResponse;

public interface ProfileService {
    ApiResponse<UserProfileResponse> getMyProfile();
    ApiResponse<UserProfileResponse> updateBasicProfile(UserProfileRequest request);
    ApiResponse<UserProfileResponse> updateMentorProfile(MentorProfileRequest request);
    ApiResponse<UserProfileResponse> updateMenteeProfile(MenteeProfileRequest request);
}
