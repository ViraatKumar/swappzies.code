package com.swapper.monolith.ItemService.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GameDataValues {
    List<String> values;
}
