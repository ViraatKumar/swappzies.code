package com.swapper.monolith.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    String displayName;
    String bio;
    @JsonProperty("avatar_url")
    String avatarUrl;
    Double lat;
    Double lng;
}
