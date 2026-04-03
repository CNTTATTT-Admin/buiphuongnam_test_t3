package vn.kurisu.mentormatch.service;

import org.springframework.web.multipart.MultipartFile;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.MentorRequestResponse;

import java.util.List;

public interface MentorRegistrationService {
    ApiResponse<String> submitRegistration(Integer userId, String bio, List<Integer> skillIds, List<MultipartFile> certificateFiles);
    ApiResponse<List<MentorRequestResponse>> getPendingRequests();
    ApiResponse<String> approveRequest(Integer profileId);
}
