package com.swapper.monolith.ItemService.mapper;

import com.swapper.monolith.ItemService.dto.CoverDto;
import com.swapper.monolith.ItemService.entity.Cover;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class CoverMapper {

    public static CoverDto toDto(Cover cover) {
        CoverDto dto = new CoverDto();
        dto.setId(cover.getId());
        dto.setUrl(cover.getUrl());
        dto.setImageId(cover.getImageId());
        dto.setWidth(cover.getWidth());
        dto.setHeight(cover.getHeight());
        dto.setAnimated(cover.getAnimated());
        dto.setAlphaChannel(cover.getAlphaChannel());
        return dto;
    }

    public Cover toEntity(CoverDto dto) {
        if (dto == null) return null;
        Cover cover = new Cover();
        BeanUtils.copyProperties(dto, cover);
        return cover;
    }
}
