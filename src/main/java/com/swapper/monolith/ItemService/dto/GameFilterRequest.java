package com.swapper.monolith.ItemService.dto;

import lombok.Data;
import org.hibernate.query.SortDirection;

import java.util.List;

@Data
public class GameFilterRequest {
    // game filters
    private String name;
    private List<String> genres;
    private List<String> platforms;
    private List<Long> themes;
    private List<Long> gameModes;
    private Double minRating;
    private Double maxRating;
    private String gameStatus;
    private String gameType;
    private Long releasedAfter;
    private Long releasedBefore;

    // page filters
    private int page;
    private int pageSize;

    // sort filters
    String sortKey;
    String sortDirection;

}
