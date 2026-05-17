package com.swapper.monolith.TradeService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTradeRequest {

    @NotBlank
    private String offeredListingId;

    @NotBlank
    private String requestedListingId;

    private String notes;
}
