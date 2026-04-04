package com.swapper.monolith.ItemService.mapper;

import com.swapper.monolith.ItemService.dto.GameDto;
import com.swapper.monolith.ItemService.entity.GameEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GameMapper {


    GameDto toDto(GameEntity entity);

    GameEntity toEntity(GameDto dto);
}
