package com.swapper.monolith.ItemService.mapper;

import com.swapper.monolith.ItemService.dto.GameDTO;
import com.swapper.monolith.ItemService.entity.GameEntity;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class GameMapper {

    public GameDTO toDTO(GameEntity entity) {
        if (entity == null) {
            return null;
        }
        GameDTO dto = new GameDTO();
        dto.setGameId(entity.getGameId());
        dto.setName(entity.getName());
        dto.setCondition(entity.getCondition());
        dto.setConsole(entity.getConsole());
        dto.setRegion(entity.getRegion());
        dto.setPrice(entity.getPrice());
        dto.setStatus(entity.getStatus());
        if (entity.getOwner() != null) {
            dto.setOwnerId(entity.getOwner().getUserId());
        }
        return dto;
    }

    public GameEntity toEntity(GameDTO dto) {
        if (dto == null) {
            return null;
        }

        GameEntity entity = new GameEntity();
        entity.setName(dto.getName());
        entity.setCondition(dto.getCondition());
        entity.setConsole(dto.getConsole());
        entity.setRegion(dto.getRegion());
        entity.setPrice(dto.getPrice());
        entity.setStatus(dto.getStatus());

        // Note: Owner is not set from DTO to prevent security issues.
        // It should be set based on the authenticated user in the service layer.
        return entity;
    }
}
