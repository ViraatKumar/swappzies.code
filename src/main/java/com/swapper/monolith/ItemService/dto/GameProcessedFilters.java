package com.swapper.monolith.ItemService.dto;

import lombok.Data;
import org.hibernate.query.SortDirection;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
public class GameProcessedFilters {
    List<Long> genreIds;
    List<Long> platformIds;
    Sort sort;
    int pageNo;
    int pageSize;
}
