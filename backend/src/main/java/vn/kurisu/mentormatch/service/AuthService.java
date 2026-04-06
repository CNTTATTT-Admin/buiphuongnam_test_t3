package vn.kurisu.mentormatch.service;

import vn.kurisu.mentormatch.dto.request.LoginRequest;
import vn.kurisu.mentormatch.dto.request.RefreshTokenRequest;
import vn.kurisu.mentormatch.dto.request.RegisterRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.AuthResponse;

public interface AuthService {
    ApiResponse<AuthResponse> register(RegisterRequest request);
    ApiResponse<AuthResponse> login(LoginRequest request);
    ApiResponse<AuthResponse> refreshToken(RefreshTokenRequest request);
    ApiResponse<Void> logout();
}
