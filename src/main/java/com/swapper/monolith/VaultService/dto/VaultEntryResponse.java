package com.swapper.monolith.VaultService.dto;

import com.swapper.monolith.ItemService.constants.Platform;
import com.swapper.monolith.VaultService.dto.constant.CompletionStatus;
import com.swapper.monolith.VaultService.entity.UserGame;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class VaultEntryResponse {
    String userGameId;
    String title;
    String platform;
    String coverUrl;
    CompletionStatus completion;
    boolean favorite;
    Date createdAt;

    public static VaultEntryResponse from(UserGame userGame) {
        return VaultEntryResponse.builder()
                .userGameId(userGame.getUserGameId())
                .title(userGame.getGame() != null ? userGame.getGame().getName() : null)
                .platform(userGame.getPlatform().getDisplayName())
                .coverUrl(userGame.getCoverUrl())
                .completion(userGame.getCompletionStatus())
                .favorite(Boolean.TRUE.equals(userGame.getFavorite()))
                .createdAt(userGame.getCreatedDate())
                .build();
    }
}
