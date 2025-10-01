package com.swapper.monolith.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class SignUpRequest {
    String name;
    String surname;
    int age;
    String email;
    String phoneNumber;
    String password;
}
