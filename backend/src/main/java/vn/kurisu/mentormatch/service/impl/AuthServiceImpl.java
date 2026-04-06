package vn.kurisu.mentormatch.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.kurisu.mentormatch.dto.request.LoginRequest;
import vn.kurisu.mentormatch.dto.request.RefreshTokenRequest;
import vn.kurisu.mentormatch.dto.request.RegisterRequest;
import vn.kurisu.mentormatch.dto.response.ApiResponse;
import vn.kurisu.mentormatch.dto.response.AuthResponse;
import vn.kurisu.mentormatch.entity.RefreshToken;
import vn.kurisu.mentormatch.entity.Role;
import vn.kurisu.mentormatch.entity.User;
import vn.kurisu.mentormatch.exception.AppException;
import vn.kurisu.mentormatch.exception.ErrorCode;
import vn.kurisu.mentormatch.repository.RoleRepository;
import vn.kurisu.mentormatch.repository.UserRepository;
import vn.kurisu.mentormatch.security.JwtTokenProvider;
import vn.kurisu.mentormatch.service.AuthService;
import vn.kurisu.mentormatch.service.RefreshTokenService;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Override
    public ApiResponse<AuthResponse> register(RegisterRequest request) {
        if (userRepository.existsByUserName(request.getUserName())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        String email = request.getEmail();
        if (email == null || email.trim().isEmpty()) {
            email = request.getUserName() + "@mentormatch.vn";
        }

        User user = User.builder()
                .userName(request.getUserName())
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getUserName()) // Default fullName to userName for now
                .isActive(true)
                .build();

        Set<Role> roles = new HashSet<>();
        // Default role for new user is MENTEE
        Role defaultRole = roleRepository.findByName("ROLE_MENTEE")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        roles.add(defaultRole);
        user.setRoles(roles);

        userRepository.save(user);

        return ApiResponse.<AuthResponse>builder()
                .code(1000)
                .message("User registered successfully")
                .result(null)
                .build();
    }

    @Override
    public ApiResponse<AuthResponse> login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.generateToken(authentication);
            User user = userRepository.findByUserName(request.getUserName())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
            String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

            AuthResponse authResponse = AuthResponse.builder()
                    .token(jwt)
                    .refreshToken(refreshToken)
                    .authenticated(true)
                    .build();

            return ApiResponse.<AuthResponse>builder()
                    .code(1000)
                    .message("Login successful")
                    .result(authResponse)
                    .build();

        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Override
    @Transactional
    public ApiResponse<AuthResponse> refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshTokenEntity = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
        User user = refreshTokenEntity.getUser();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUserName(),
                null,
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .toList()
        );

        String accessToken = tokenProvider.generateToken(authentication);
        String rotatedRefreshToken = refreshTokenService.rotateRefreshToken(refreshTokenEntity).getToken();

        AuthResponse authResponse = AuthResponse.builder()
                .token(accessToken)
                .refreshToken(rotatedRefreshToken)
                .authenticated(true)
                .build();

        return ApiResponse.<AuthResponse>builder()
                .code(1000)
                .message("Refresh token successful")
                .result(authResponse)
                .build();
    }

    @Override
    public ApiResponse<Void> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getName() != null) {
            userRepository.findByUserName(authentication.getName())
                    .ifPresent(user -> refreshTokenService.revokeAllValidTokensByUser(user.getId()));
        }

        SecurityContextHolder.clearContext();

        return ApiResponse.<Void>builder()
                .message("Logout successful")
                .build();
    }
}
