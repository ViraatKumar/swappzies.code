package com.swapper.monolith.WishlistService.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToWishlistRequest {
    @NotNull
    @JsonProperty("game_id")
    String gameId;
}
