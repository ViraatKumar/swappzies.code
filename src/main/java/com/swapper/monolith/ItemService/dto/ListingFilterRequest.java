package com.swapper.monolith.ItemService.dto;

import lombok.Data;

@Data
public class ListingFilterRequest {
    Long gameId;
    String platform;    // maps to Platform enum
    String listingType;   // maps to OfferType enum — null = all
    String condition;     // maps to Condition enum
    Double maxPrice;
    // Geospatial (PostGIS stub)
    Double lat;
    Double lng;
    Double radiusKm;
    // Pagination
    int page;
    int size = 20;
}
