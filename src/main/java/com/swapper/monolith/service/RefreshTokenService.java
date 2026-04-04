package com.swapper.monolith.service;

import com.swapper.monolith.exception.CustomExceptions.TokenExpiredException;
import com.swapper.monolith.exception.enums.ApiResponses;
import com.swapper.monolith.model.RefreshToken;
import com.swapper.monolith.model.User;
import com.swapper.monolith.repository.RefreshTokenRepository;
import com.swapper.monolith.security.SecurityConfiguration.SecurityConfiguration;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RefreshTokenService {

    RefreshTokenRepository refreshTokenRepository;
    SecurityConfiguration securityConfiguration;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plusMillis(securityConfiguration.getRefreshTokenExpiration()))
                .createdAt(Instant.now())
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenExpiredException(ApiResponses.REFRESH_TOKEN_EXPIRED);
        }
        return token;
    }

    @Transactional
    public RefreshToken rotateToken(RefreshToken oldToken) {
        refreshTokenRepository.delete(oldToken);
        return createRefreshToken(oldToken.getUser());
    }

    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
}
