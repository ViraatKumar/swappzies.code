package com.swapper.monolith.dto;

import com.swapper.monolith.model.User;
import com.swapper.monolith.model.UserToken;
import com.swapper.monolith.model.enums.TokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class UserTokenDto {
    String token;
    User user;
    TokenType type;
    Instant expiryDate;
    Instant createdAt;
    boolean used;
    public static UserTokenDto fromEntity(UserToken userToken) {
        return new UserTokenDto(userToken.getToken(),userToken.getUser(),userToken.getType(),userToken.getExpiryDate(),userToken.getCreatedAt(),userToken.isUsed());
    }
}
