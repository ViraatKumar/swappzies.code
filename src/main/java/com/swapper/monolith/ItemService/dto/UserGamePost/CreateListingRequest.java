package com.swapper.monolith.ItemService.dto.UserGamePost;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swapper.monolith.ItemService.constants.OfferType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateListingRequest {
    @NotNull
    @JsonProperty("game_id")
    String gameId;

    String condition;

    @NotNull
    String platform;

    @NotEmpty
    List<OfferType> offerTypes;

    Double price;
}
