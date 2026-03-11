package vn.kurisu.mentormatch.service;

import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.UserProfileResponse;

import vn.kurisu.mentormatch.dto.response.TimeSlotResponse;

import java.util.List;

public interface PublicMentorService {
    ApiResponse<List<UserProfileResponse>> getAllPublicMentors();
    ApiResponse<UserProfileResponse> getPublicMentorProfile(Integer id);
    ApiResponse<List<TimeSlotResponse>> getPublicMentorTimeSlots(Integer mentorId);
}
