package vn.kurisu.mentormatch.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.kurisu.mentormatch.dto.request.RoleRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.RoleResponse;
import vn.kurisu.mentormatch.entity.Role;
import vn.kurisu.mentormatch.exception.AppException;
import vn.kurisu.mentormatch.exception.ErrorCode;
import vn.kurisu.mentormatch.repository.RoleRepository;
import vn.kurisu.mentormatch.service.RoleService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public ApiResponse<RoleResponse> create(RoleRequest request) {
        if (roleRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Role already exists");
        }

        Role role = Role.builder()
                .name(request.getName())
                .build();

        role = roleRepository.save(role);

        RoleResponse response = RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .build();

        return ApiResponse.<RoleResponse>builder()
                .result(response)
                .build();
    }

    @Override
    public ApiResponse<List<RoleResponse>> getAll() {
        List<RoleResponse> roles = roleRepository.findAll()
                .stream()
                .map(role -> RoleResponse.builder()
                        .id(role.getId())
                        .name(role.getName())
                        .build())
                .collect(Collectors.toList());

        return ApiResponse.<List<RoleResponse>>builder()
                .result(roles)
                .build();
    }

    @Override
    public ApiResponse<Void> delete(Integer id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        roleRepository.delete(role);

        return ApiResponse.<Void>builder()
                .message("Role has been deleted")
                .build();
    }
}
