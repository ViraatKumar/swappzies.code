package com.swapper.monolith.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class PublicUserProfileResponse {

    String username;
    String dispalyName;
    String bio;
    String avatarUrl;
    double lat;
    double lng;
    String memberSince;
    String tradeCount;
    String activeListingCount;
}
