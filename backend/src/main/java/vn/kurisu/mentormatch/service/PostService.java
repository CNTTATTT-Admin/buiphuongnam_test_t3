package vn.kurisu.mentormatch.service;

import vn.kurisu.mentormatch.dto.request.PostCreationRequest;
import vn.kurisu.mentormatch.dto.request.PostUpdateRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.PostResponse;

import java.util.List;

public interface PostService {
    ApiResponse<PostResponse> create(PostCreationRequest request);
    ApiResponse<List<PostResponse>> getAll();
    ApiResponse<PostResponse> getById(Integer id);
    ApiResponse<PostResponse> update(Integer id, PostUpdateRequest request);
    ApiResponse<Void> delete(Integer id);
}
