package com.swapper.monolith.controller;

import com.swapper.monolith.dto.LoginRequest;
import com.swapper.monolith.dto.LoginResponse;
import com.swapper.monolith.dto.SignUpRequest;
import com.swapper.monolith.service.AuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {
    AuthService authService;
    @PostMapping("/user-login")
    public ResponseEntity<LoginResponse> userLogin(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/user-register")
    public ResponseEntity<String> userRegister(@RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok(authService.register(signUpRequest));
    }
}
