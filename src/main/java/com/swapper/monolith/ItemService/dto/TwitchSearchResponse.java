package com.swapper.monolith.ItemService.dto;

import lombok.Data;
import java.util.List;

@Data
public class TwitchSearchResponse {
    List<IgdbGameDto> gameDTOList;
}
