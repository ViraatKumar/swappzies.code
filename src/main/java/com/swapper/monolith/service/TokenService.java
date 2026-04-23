package com.swapper.monolith.service;

import com.swapper.monolith.dto.UserTokenDto;
import com.swapper.monolith.exception.CustomExceptions.TokenExpiredException;
import com.swapper.monolith.exception.ResourceNotFoundException;
import com.swapper.monolith.exception.enums.ApiResponses;
import com.swapper.monolith.model.User;
import com.swapper.monolith.model.UserToken;
import com.swapper.monolith.model.enums.TokenType;
import com.swapper.monolith.repository.UserTokenRepository;
import com.swapper.monolith.security.SecurityConfiguration.SecurityConfiguration;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenService {

    UserTokenRepository userTokenRepository;
    SecurityConfiguration securityConfiguration;
    static final String HASH_ALGORITHM = "SHA-256";

    @Transactional
    public String createToken(User user, TokenType type) {
        if (type == TokenType.PASSWORD_RESET) {
            userTokenRepository.deleteByUserAndType(user, type);
        }
        String rawToken = UUID.randomUUID().toString();
        long expirationMillis = switch (type) {
            case REFRESH -> securityConfiguration.getRefreshTokenExpiration();
            case PASSWORD_RESET -> securityConfiguration.getPasswordResetTokenExpiration();
        };
        UserToken token = UserToken.builder()
                .token(hash(rawToken))
                .user(user)
                .type(type)
                .expiryDate(Instant.now().plusMillis(expirationMillis))
                .createdAt(Instant.now())
                .used(false)
                .build();
        userTokenRepository.save(token);
        return rawToken;
    }

    public UserTokenDto findToken(String rawToken, TokenType type) {
        Optional<UserToken> userToken = userTokenRepository.findByTokenAndType(hash(rawToken), type);
        return userToken.map(UserTokenDto::fromEntity).orElse(null);
    }

    public void verifyRefreshExpiration(UserTokenDto token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            userTokenRepository.deleteByTokenAndType(token.getToken(), token.getType());
            throw new TokenExpiredException(ApiResponses.REFRESH_TOKEN_EXPIRED);
        }
    }

    @Transactional
    public String rotateToken(UserTokenDto oldToken) {
        User user = oldToken.getUser();
        userTokenRepository.deleteByTokenAndType(oldToken.getToken(), oldToken.getType());
        return createToken(user, TokenType.REFRESH);
    }

    @Transactional
    public void deleteAllTokens(User user, TokenType type) {
        userTokenRepository.deleteByUserAndType(user, type);
    }

    public UserTokenDto validatePasswordResetToken(String rawToken) {
        UserToken token = userTokenRepository.findByTokenAndType(hash(rawToken), TokenType.PASSWORD_RESET)
                .orElseThrow(() -> new BadCredentialsException(ApiResponses.PASSWORD_RESET_TOKEN_INVALID.getMessage()));

        if (token.isUsed()) {
            throw new BadCredentialsException(ApiResponses.PASSWORD_RESET_TOKEN_INVALID.getMessage());
        }

        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new TokenExpiredException(ApiResponses.PASSWORD_RESET_TOKEN_EXPIRED);
        }

        return UserTokenDto.fromEntity(token);
    }

    @Transactional
    public void markUsed(String token) {
        UserToken userToken = userTokenRepository.findByToken(token).orElse(null);
        if(userToken == null) {
            throw new ResourceNotFoundException("Token not found");
        }
        userToken.setUsed(true);
        userTokenRepository.save(userToken);
    }

    private String hash(String token) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] bytes = md.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(HASH_ALGORITHM + " not available", e);
        }
    }
}
