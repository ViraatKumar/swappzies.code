package com.swapper.monolith.service;

import com.swapper.monolith.dto.ApiResponse;
import com.swapper.monolith.dto.EmailSignUpRequest;
import com.swapper.monolith.dto.LoginRequest;
import com.swapper.monolith.dto.LoginResponse;
import com.swapper.monolith.dto.RefreshTokenRequest;
import com.swapper.monolith.exception.CustomExceptions.TokenExpiredException;
import com.swapper.monolith.exception.enums.ApiResponses;
import com.swapper.monolith.model.RefreshToken;
import com.swapper.monolith.model.User;
import com.swapper.monolith.security.utils.JwtUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {
    AuthenticationManager authenticationManager;
    JwtUtil jwtUtil;
    UserService userService;
    RefreshTokenService refreshTokenService;

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword())
        );
        if (!authentication.isAuthenticated()) {
            throw new BadCredentialsException("Invalid username or password");
        }
        String accessToken = jwtUtil.generateToken(new HashMap<>(), loginRequest.getUsername());
        User user = userService.findByUsername(loginRequest.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        return LoginResponse.builder()
                .jwtToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public LoginResponse refresh(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BadCredentialsException(ApiResponses.REFRESH_TOKEN_INVALID.getMessage()));
        refreshTokenService.verifyExpiration(refreshToken);
        RefreshToken newRefreshToken = refreshTokenService.rotateToken(refreshToken);
        String accessToken = jwtUtil.generateToken(new HashMap<>(), newRefreshToken.getUser().getUsername());
        return LoginResponse.builder()
                .jwtToken(accessToken)
                .refreshToken(newRefreshToken.getToken())
                .build();
    }

    public void logout(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new BadCredentialsException(ApiResponses.REFRESH_TOKEN_INVALID.getMessage()));
        refreshTokenService.deleteByUser(refreshToken.getUser());
    }

    public String register(@RequestBody EmailSignUpRequest emailSignUpRequest) {
        return userService.addUser(emailSignUpRequest);
    }

    public ApiResponse<Boolean> checkUsername(String username) {
        return userService.checkUsername(username);
    }
}
