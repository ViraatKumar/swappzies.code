package com.swapper.monolith.ItemService.dto;

import com.swapper.monolith.model.User;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class UserProfileResponse {
    String username;
    String displayName;
    String bio;
    String avatarUrl;
    Double lat;
    Double lng;
    Date memberSince;

    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .lat(user.getLat())
                .lng(user.getLng())
                .memberSince(user.getCreatedAt() != null ? Date.from(user.getCreatedAt()) : null)
                .build();
    }
}
