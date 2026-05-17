package com.swapper.monolith.TradeService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class CreateRentalRequest {

    @NotBlank
    private String listingId;

    private Instant rentalStartDate;

    private Instant rentalEndDate;

    private String notes;
}
