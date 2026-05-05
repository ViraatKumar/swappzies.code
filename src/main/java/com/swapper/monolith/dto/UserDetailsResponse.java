package com.swapper.monolith.dto;

import com.swapper.monolith.model.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetailsResponse {
    String userId;
    String username;
    String email;
    String phoneNumber;
    String displayName;
    String bio;
    String avatarUrl;

    public static UserDetailsResponse from(User user){
        return UserDetailsResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNo())
                .displayName(user.getDisplayName())
                .bio(user.getBio())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}
