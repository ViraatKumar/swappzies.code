package com.swapper.monolith.dto;

import com.swapper.monolith.TradeService.dto.Game;
import lombok.Data;

import java.util.List;

@Data
public class UserItemsResponse {
    private List<Game> items;
}
