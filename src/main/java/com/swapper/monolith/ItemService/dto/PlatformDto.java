package com.swapper.monolith.ItemService.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlatformDto {
    Long id;
    String abbreviation;
    String alternativeName;
    String checksum;
    Date createdAt;
    Integer generation;
    String name;
    Long platformFamily;
    Long platformLogo;
    Long platformType;
    String slug;
    String summary;
    Date updatedAt;
    String url;
    List<Long> versions;
    List<Long> websites;
}
