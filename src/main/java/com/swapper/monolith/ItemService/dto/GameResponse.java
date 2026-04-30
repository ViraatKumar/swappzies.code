package com.swapper.monolith.ItemService.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GameResponse {
    Long id;
    String title;
    List<String> platform;
    List<String> genre;
    String condition;
    String types;
    Double sellPrice;
    String coverImageUrl;
    Date postedAt;
    List<String> ownersList;
}
