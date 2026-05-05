package com.swapper.monolith.ItemService.dto.FilterGames;

import com.swapper.monolith.ItemService.dto.GameDto;
import com.swapper.monolith.ItemService.service.GenreService;
import com.swapper.monolith.ItemService.service.PlatformService;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class GameResponse {
    Long id;
    String name;
    List<String> platform;
    List<String> genre;
}
