package com.swapper.monolith.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
