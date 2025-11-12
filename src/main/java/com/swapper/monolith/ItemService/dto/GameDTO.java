package com.swapper.monolith.ItemService.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swapper.monolith.ItemService.constants.ItemStatus;
import com.swapper.monolith.TradeService.dto.enums.Condition;
import com.swapper.monolith.TradeService.dto.enums.Console;
import jakarta.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class GameDTO {
    @JsonProperty("game_id")
    String gameId;
    String name;
    Condition condition;
    Console console;
    String region;
    BigDecimal price;
    ItemStatus status;
    @JsonProperty("owner_id")
    String ownerId;
    @JsonProperty("created_at")
    Date createdAt;
    @JsonProperty("updated_at")
    Date updatedAt;
}
