package vn.kurisu.mentormatch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.kurisu.mentormatch.entity.RefreshToken;
import vn.kurisu.mentormatch.entity.User;
import vn.kurisu.mentormatch.exception.AppException;
import vn.kurisu.mentormatch.exception.ErrorCode;
import vn.kurisu.mentormatch.repository.RefreshTokenRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-expiration:604800000}")
    private long refreshTokenExpirationInMs;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        revokeAllValidTokensByUser(user.getId());

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(LocalDateTime.now().plus(refreshTokenExpirationInMs, ChronoUnit.MILLIS))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.REFRESH_TOKEN_INVALID));

        if (Boolean.TRUE.equals(refreshToken.getRevoked())) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        // Ensure lazy relation is initialized for downstream access in service layer.
        refreshToken.getUser().getUserName();

        return refreshToken;
    }

    @Transactional
    public RefreshToken rotateRefreshToken(RefreshToken currentToken) {
        currentToken.setRevoked(true);
        refreshTokenRepository.save(currentToken);

        RefreshToken newRefreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(currentToken.getUser())
                .expiresAt(LocalDateTime.now().plus(refreshTokenExpirationInMs, ChronoUnit.MILLIS))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(newRefreshToken);
    }

    @Transactional
    public void revokeAllValidTokensByUser(Integer userId) {
        List<RefreshToken> activeTokens = refreshTokenRepository.findByUserIdAndRevokedFalse(userId);
        if (activeTokens.isEmpty()) {
            return;
        }

        activeTokens.forEach(token -> token.setRevoked(true));
        refreshTokenRepository.saveAll(activeTokens);
    }
}
