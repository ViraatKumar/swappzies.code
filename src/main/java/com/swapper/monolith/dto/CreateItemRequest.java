package com.swapper.monolith.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swapper.monolith.dto.enums.Condition;
import com.swapper.monolith.dto.enums.Console;
import com.swapper.monolith.model.ItemImage;
import com.swapper.monolith.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
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
