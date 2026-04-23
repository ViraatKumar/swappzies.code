package com.swapper.monolith.ItemService.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

@Data
public class CreateListingRequest {
    @JsonProperty("game_id")
    String gameId;

    @JsonProperty("gameName")
    String gameName;
}
