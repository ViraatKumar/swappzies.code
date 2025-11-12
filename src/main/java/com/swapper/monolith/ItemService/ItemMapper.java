package com.swapper.monolith.ItemService;

import com.swapper.monolith.ItemService.dto.GameDTO;
import com.swapper.monolith.ItemService.entity.GameEntity;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {
    public GameDTO toGameDTO(GameEntity gameEntity) {
        return GameDTO.builder()
                .gameId(gameEntity.getGameId())
                .name(gameEntity.getName())
                .price(gameEntity.getPrice())
                .condition(gameEntity.getCondition())
                .console(gameEntity.getConsole())
                .ownerId(gameEntity.getOwner().getUserId())
                .status(gameEntity.getStatus())
                .region(gameEntity.getRegion())
                .createdAt(gameEntity.getCreatedAt())
                .updatedAt(gameEntity.getUpdatedAt())
                .build();
    }
}
