package com.swapper.monolith.ItemService.dto.UserGamePost;

import com.swapper.monolith.ItemService.constants.ListingState;
import com.swapper.monolith.ItemService.constants.OfferType;
import lombok.Data;

import java.util.List;

@Data
public class UpdateListingRequest {
    String condition;           // nullable — maps to Condition
    Double price;               // nullable — triggers pricing stub
    List<OfferType> offerTypes; // nullable — replaces current list
    String description;         // nullable
    ListingState listingState;  // nullable — activate/deactivate listing
}
