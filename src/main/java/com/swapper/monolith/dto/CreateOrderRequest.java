package com.swapper.monolith.dto;

import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @NotNull
        String buyerId,
        @NotNull
        String gameId,
        @NotNull
        String sellerId
) {
}
