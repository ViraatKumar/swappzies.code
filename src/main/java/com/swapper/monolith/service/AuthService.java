package com.swapper.monolith.service;

import com.swapper.monolith.dto.*;
import com.swapper.monolith.exception.enums.ApiResponses;
import com.swapper.monolith.model.User;
import com.swapper.monolith.model.UserToken;
import com.swapper.monolith.model.enums.TokenType;
import com.swapper.monolith.repository.UserRepository;
import com.swapper.monolith.security.utils.JwtUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    TokenService tokenService;
    EmailService emailService;
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;

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
        String rawRefreshToken = tokenService.createToken(user, TokenType.REFRESH);
        return LoginResponse.builder()
                .jwtToken(accessToken)
                .refreshToken(rawRefreshToken)
                .build();
    }

    public LoginResponse refresh(RefreshTokenRequest request) {
        UserTokenDto refreshToken = tokenService.findToken(request.getRefreshToken(), TokenType.REFRESH);
        if(refreshToken == null) {
            throw new BadCredentialsException(ApiResponses.REFRESH_TOKEN_INVALID.getMessage());
        }
        tokenService.verifyRefreshExpiration(refreshToken);
        String newRawToken = tokenService.rotateToken(refreshToken);
        String accessToken = jwtUtil.generateToken(new HashMap<>(), refreshToken.getUser().getUsername());
        return LoginResponse.builder()
                .jwtToken(accessToken)
                .refreshToken(newRawToken)
                .build();
    }

    public void logout(RefreshTokenRequest request) {
        UserTokenDto refreshToken = tokenService.findToken(request.getRefreshToken(), TokenType.REFRESH);
        if(refreshToken == null) {
            throw new BadCredentialsException(ApiResponses.REFRESH_TOKEN_INVALID.getMessage());
        }
        tokenService.deleteAllTokens(refreshToken.getUser(), TokenType.REFRESH);
    }

    public String register(@RequestBody EmailSignUpRequest emailSignUpRequest) {
        return userService.addUser(emailSignUpRequest);
    }

    public ApiResponse<Boolean> checkUsername(String username) {
        return userService.checkUsername(username);
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user -> {
            String rawToken = tokenService.createToken(user, TokenType.PASSWORD_RESET);
            emailService.sendPasswordResetEmail(user.getEmail(), rawToken);
        });
        // always returns normally — prevents email enumeration
    }

    public void resetPassword(ResetPasswordRequest request) {
        UserTokenDto resetToken = tokenService.validatePasswordResetToken(request.getToken());
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        tokenService.markUsed(resetToken.getToken());
    }
}
