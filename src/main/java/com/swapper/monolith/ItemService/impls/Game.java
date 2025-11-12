package com.swapper.monolith.ItemService.impls;

import com.swapper.monolith.ItemService.ItemMapper;
import com.swapper.monolith.ItemService.ItemService;
import com.swapper.monolith.ItemService.dto.GameDTO;
import com.swapper.monolith.TradeService.dto.enums.ItemType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serivce eima2
 */
@Service
@RequiredArgsConstructor
public class Game implements ItemService<GameDTO> {

    ItemMapper itemMapper;

    @Override
    public GameDTO create(GameDTO createRequest) {
        return null;
    }

    @Override
    public GameDTO update(String id, GameDTO updateRequest) {
        return null;
    }

    @Override
    public void delete(String id) {

    }

    @Override
    public List<GameDTO> getUserItems(String userId) {
        return List.of();
    }

    @Override
    public GameDTO getUserItem(String userId, String itemId) {
        return null;
    }

    @Override
    public ItemType getItemType() {
        return null;
    }
}
