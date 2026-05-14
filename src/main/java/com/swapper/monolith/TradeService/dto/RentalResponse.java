package com.swapper.monolith.TradeService.dto;

import com.swapper.monolith.TradeService.dto.constant.RentalStatus;
import com.swapper.monolith.TradeService.entity.Rental;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Date;

@Getter
@Builder
public class RentalResponse {

    private String rentalId;
    private String renterUsername;
    private String ownerUsername;

    private String listingId;
    private String gameName;
    private String platform;

    private RentalStatus status;
    private String notes;

    private Instant rentalStartDate;
    private Instant rentalEndDate;
    private Instant returnedAt;
    private Date createdDate;

    public static RentalResponse from(Rental rental) {
        return RentalResponse.builder()
                .rentalId(rental.getRentalId())
                .renterUsername(rental.getRenter().getUsername())
                .ownerUsername(rental.getOwner().getUsername())
                .listingId(rental.getListing().getListingId())
                .gameName(rental.getListing().getGame().getName())
                .platform(rental.getListing().getPlatform().name())
                .status(rental.getStatus())
                .notes(rental.getNotes())
                .rentalStartDate(rental.getRentalStartDate())
                .rentalEndDate(rental.getRentalEndDate())
                .returnedAt(rental.getReturnedAt())
                .createdDate(rental.getCreatedDate())
                .build();
    }
}
