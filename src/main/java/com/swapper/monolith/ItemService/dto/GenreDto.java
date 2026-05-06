package com.swapper.monolith.ItemService.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class GenreDto {
    Long id;
    String name;
    String checksum;
    String slug;
    String url;
}
