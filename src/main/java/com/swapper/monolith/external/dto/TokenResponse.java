package com.swapper.monolith.external.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public record TokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") long expiresIn,
        @JsonProperty("token_type") String tokenType,
        Date createdAt
) {
    // Constructor to set createdAt automatically if not provided (e.g. from JSON)
    public TokenResponse(String accessToken, long expiresIn, String tokenType, Date createdAt) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
        this.createdAt = (createdAt == null) ? new Date() : createdAt;
    }

    public boolean isExpired() {
        if (createdAt == null) return true;
        long expirationTimeInMillis = createdAt.getTime() + (expiresIn * 1000);
        // Add a buffer of 60 seconds to be safe
        return System.currentTimeMillis() > (expirationTimeInMillis - 60000);
    }
}
