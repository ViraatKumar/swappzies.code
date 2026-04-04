package com.swapper.monolith.controller;

import com.swapper.monolith.dto.ApiResponse;
import com.swapper.monolith.dto.EmailSignUpRequest;
import com.swapper.monolith.dto.LoginRequest;
import com.swapper.monolith.dto.LoginResponse;
import com.swapper.monolith.dto.RefreshTokenRequest;
import com.swapper.monolith.service.AuthService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth/v1")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;
    @PostMapping("/user-login")
    public ResponseEntity<LoginResponse> userLogin(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/user-register")
    public ResponseEntity<String> userRegister(@Valid @RequestBody EmailSignUpRequest emailSignUpRequest) {
        return ResponseEntity.ok(authService.register(emailSignUpRequest));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/username-exists")
    public ResponseEntity<ApiResponse<Boolean>> checkUsername(@RequestParam("username") String username){
        ApiResponse<Boolean> response = authService.checkUsername(username);
        return ResponseEntity.status(response.status()).body(response);
    }
}
