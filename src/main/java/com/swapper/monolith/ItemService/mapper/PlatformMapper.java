package com.swapper.monolith.ItemService.mapper;

import com.swapper.monolith.ItemService.dto.PlatformDto;
import com.swapper.monolith.ItemService.entity.PlatformEntity;

public class PlatformMapper {

    public static PlatformDto toDto(PlatformEntity entity) {
        return PlatformDto.builder()
                .id(entity.getId())
                .abbreviation(entity.getAbbreviation())
                .alternativeName(entity.getAlternativeName())
                .checksum(entity.getChecksum())
                .createdAt(entity.getCreatedAt())
                .generation(entity.getGeneration())
                .name(entity.getName())
                .platformFamily(entity.getPlatformFamily())
                .platformLogo(entity.getPlatformLogo())
                .platformType(entity.getPlatformType())
                .slug(entity.getSlug())
                .summary(entity.getSummary())
                .updatedAt(entity.getUpdatedAt())
                .url(entity.getUrl())
                .versions(entity.getVersions())
                .websites(entity.getWebsites())
                .build();
    }

    public static PlatformEntity toEntity(PlatformDto dto) {
        PlatformEntity entity = new PlatformEntity();
        entity.setId(dto.getId());
        entity.setAbbreviation(dto.getAbbreviation());
        entity.setAlternativeName(dto.getAlternativeName());
        entity.setChecksum(dto.getChecksum());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setGeneration(dto.getGeneration());
        entity.setName(dto.getName());
        entity.setPlatformFamily(dto.getPlatformFamily());
        entity.setPlatformLogo(dto.getPlatformLogo());
        entity.setPlatformType(dto.getPlatformType());
        entity.setSlug(dto.getSlug());
        entity.setSummary(dto.getSummary());
        entity.setUpdatedAt(dto.getUpdatedAt());
        entity.setUrl(dto.getUrl());
        entity.setVersions(dto.getVersions());
        entity.setWebsites(dto.getWebsites());
        return entity;
    }
}
