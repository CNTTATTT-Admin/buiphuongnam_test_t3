package vn.kurisu.mentormatch.service;

import vn.kurisu.mentormatch.dto.request.UserCreationRequest;
import vn.kurisu.mentormatch.dto.request.UserUpdateRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    ApiResponse<UserResponse> create(UserCreationRequest request);
    ApiResponse<List<UserResponse>> getAll();
    ApiResponse<List<UserResponse>> getMentors();
    ApiResponse<UserResponse> getById(Integer id);
    ApiResponse<UserResponse> update(Integer id, UserUpdateRequest request);
    ApiResponse<String> updateAvatar(org.springframework.web.multipart.MultipartFile file);
    ApiResponse<Void> delete(Integer id);
}
