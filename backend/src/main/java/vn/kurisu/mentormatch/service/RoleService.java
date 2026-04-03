package vn.kurisu.mentormatch.service;

import vn.kurisu.mentormatch.dto.request.RoleRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.RoleResponse;

import java.util.List;

public interface RoleService {
    ApiResponse<RoleResponse> create(RoleRequest request);
    ApiResponse<List<RoleResponse>> getAll();
    ApiResponse<Void> delete(Integer id);
}
