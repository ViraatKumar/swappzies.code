package com.swapper.monolith.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swapper.monolith.TradeService.dto.enums.Condition;
import com.swapper.monolith.TradeService.dto.enums.Console;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateItemRequest(
        @NotNull
        String title,
        @NotNull
        String description,
        @NotNull
        @JsonProperty("item_condition")
        Condition condition,
        @NotNull
        Console console,
        @NotNull
        Double price,
        @NotNull
        String userId,
        @NotNull
        List<String> imageUrls) {
}
