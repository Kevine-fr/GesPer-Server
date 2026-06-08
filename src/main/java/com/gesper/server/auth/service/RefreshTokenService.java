package com.gesper.server.auth.service;

import com.gesper.server.auth.entity.RefreshToken;
import com.gesper.server.auth.repository.RefreshTokenRepository;
import com.gesper.server.common.exception.UnauthorizedException;
import com.gesper.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final RefreshTokenRepository repository;

    @Value("${gesper.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Transactional
    public RefreshToken create(User user) {
        String token = generateToken();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiresAt(Instant.now().plusMillis(refreshTokenExpiration))
                .revoked(false)
                .build();
        return repository.save(refreshToken);
    }

    @Transactional
    public RefreshToken verifyAndRotate(String token, User user) {
        RefreshToken existing = repository.findByToken(token)
                .orElseThrow(() -> new UnauthorizedException("Refresh token invalide."));
        if (existing.isRevoked() || existing.getExpiresAt().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token expiré ou révoqué — veuillez vous reconnecter.");
        }
        if (!existing.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("Refresh token invalide.");
        }
        existing.setRevoked(true);
        repository.save(existing);
        return create(user);
    }

    @Transactional
    public void revokeAll(User user) {
        repository.revokeAllByUser(user);
    }

    private String generateToken() {
        byte[] buffer = new byte[48];
        RANDOM.nextBytes(buffer);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(buffer);
    }
}
