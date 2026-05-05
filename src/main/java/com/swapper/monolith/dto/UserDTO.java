package com.swapper.monolith.dto;

import com.swapper.monolith.model.Roles;
import com.swapper.monolith.model.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserDTO {
    String userId;
    String username;
    String email;
    String phoneNo;
    String displayName;
    String bio;
    String avatarUrl;
    Double lat;
    Double lng;
    Instant createdAt;
    Set<Roles> roles;

    public static UserDTO from(User user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNo(user.getPhoneNo())
                .displayName(user.getDisplayName())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .lat(user.getLat())
                .lng(user.getLng())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles())
                .build();
    }
}
