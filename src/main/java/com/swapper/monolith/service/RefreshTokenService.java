package com.swapper.monolith.service;

import com.swapper.monolith.ItemService.dto.RefreshTokenResponse;
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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Ref;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RefreshTokenService {

    RefreshTokenRepository refreshTokenRepository;
    SecurityConfiguration securityConfiguration;
    static final String HASH_ALGORITHM = "SHA-256";

    @Transactional
    public RefreshTokenResponse createRefreshToken(User user) {
        String rawToken = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.builder()
                .token(hash(rawToken))
                .user(user)
                .expiryDate(Instant.now().plusMillis(securityConfiguration.getRefreshTokenExpiration()))
                .createdAt(Instant.now())
                .build();
        refreshTokenRepository.save(refreshToken);
        return new RefreshTokenResponse(user,rawToken
        );
    }
    public Optional<RefreshToken> findByToken(String token){
        return refreshTokenRepository.findByToken(hash(token));
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenExpiredException(ApiResponses.REFRESH_TOKEN_EXPIRED);
        }
        return token;
    }

    @Transactional
    public RefreshTokenResponse rotateToken(RefreshToken oldToken) {
        refreshTokenRepository.delete(oldToken);
        return createRefreshToken(oldToken.getUser());
    }

    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    private String hash(String token){
        try{
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] bytes = md.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(HASH_ALGORITHM + "Not available" + e);
        }
    }
}
