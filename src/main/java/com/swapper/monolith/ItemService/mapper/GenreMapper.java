package com.swapper.monolith.ItemService.mapper;

import com.swapper.monolith.ItemService.dto.GameDto;
import com.swapper.monolith.ItemService.dto.GenreDto;
import com.swapper.monolith.ItemService.entity.GenreEntity;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {
    public static GenreDto toDto(GenreEntity genreEntity){
        return GenreDto.builder()
                .id(genreEntity.getId())
                .name(genreEntity.getName())
                .url(genreEntity.getUrl())
                .slug(genreEntity.getSlug())
                .checksum(genreEntity.getChecksum())
                .build();
    }

    public static GenreEntity toEntity(GenreDto genreDto){
        GenreEntity genreEntity = new GenreEntity();
        genreEntity.setId(genreDto.getId());
        genreEntity.setName(genreDto.getName());
        genreEntity.setUrl(genreDto.getUrl());
        genreEntity.setSlug(genreDto.getSlug());
        genreEntity.setChecksum(genreDto.getChecksum());
        return genreEntity;
    }
}
