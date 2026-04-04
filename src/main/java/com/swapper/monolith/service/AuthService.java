package com.swapper.monolith.service;

import com.swapper.monolith.dto.ApiResponse;
import com.swapper.monolith.dto.EmailSignUpRequest;
import com.swapper.monolith.dto.LoginRequest;
import com.swapper.monolith.dto.LoginResponse;
import com.swapper.monolith.security.utils.JwtUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
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

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword())
        );
        if(!authentication.isAuthenticated()){
            throw new BadCredentialsException("Invalid username or password");
        }
        String token = jwtUtil.generateToken(new HashMap<>(),loginRequest.getUsername());
        return new LoginResponse(token);
    }

    public String register(@RequestBody EmailSignUpRequest emailSignUpRequest) {
        return userService.addUser(emailSignUpRequest);
    }

    public ApiResponse<Boolean> checkUsername(String username){
        return userService.checkUsername(username);
    }
}
