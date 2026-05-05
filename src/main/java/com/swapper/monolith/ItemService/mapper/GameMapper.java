package com.swapper.monolith.ItemService.mapper;

import com.swapper.monolith.ItemService.dto.GameDto;
import com.swapper.monolith.ItemService.entity.GameEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class GameMapper {

    public GameDto toDto(GameEntity entity) {
        if (entity == null) return null;
        GameDto dto = new GameDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    public GameEntity toEntity(GameDto dto) {
        if (dto == null) return null;
        GameEntity entity = new GameEntity();
        BeanUtils.copyProperties(dto, entity);
        return entity;
    }
}
