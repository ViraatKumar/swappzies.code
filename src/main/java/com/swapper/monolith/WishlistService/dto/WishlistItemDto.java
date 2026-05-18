package com.swapper.monolith.WishlistService.dto;

import com.swapper.monolith.WishlistService.entity.WishlistItem;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Date;

@Data
@Builder
public class WishlistItemDto {
    String wishlistItemId;
    Long gameId;
    String gameName;
    String coverUrl;
    Date addedAt;
    public static WishlistItemDto from(WishlistItem item, String coverUrl) {
        return WishlistItemDto.builder()
                .wishlistItemId(item.getWishlistItemId())
                .gameId(item.getGame().getId())
                .gameName(item.getGame().getName())
                .coverUrl(coverUrl)
                .addedAt(item.getCreatedDate())
                .build();
    }
}
