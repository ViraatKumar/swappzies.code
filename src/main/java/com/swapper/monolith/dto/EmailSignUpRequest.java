package com.swapper.monolith.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class EmailSignUpRequest {

    @NotBlank
    String username;

    @NotBlank(message="Email is Required")
    @Email
    String email;

    @NotBlank(message="Password is required")
    String password;
}
