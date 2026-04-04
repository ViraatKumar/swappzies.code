package com.swapper.monolith.ItemService.dto;

import com.swapper.monolith.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefreshTokenResponse {
    private User user;
    private String refreshToken;
}
