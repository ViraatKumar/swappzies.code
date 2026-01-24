package com.swapper.monolith.ItemService.dto;

import lombok.Data;

import java.util.List;

@Data
public class GameSearchResponse {
    List<GameDto> gameDtoList;
    public GameSearchResponse(List<GameDto> gameDtos) {
        this.gameDtoList = gameDtos;
    }
}
