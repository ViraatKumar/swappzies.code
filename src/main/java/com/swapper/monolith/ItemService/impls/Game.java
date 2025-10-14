package com.swapper.monolith.ItemService.impls;

import com.swapper.monolith.ItemService.ItemService;
import com.swapper.monolith.TradeService.dto.enums.ItemType;
import com.swapper.monolith.dto.ApiResponse;
import com.swapper.monolith.dto.CreateItemRequest;
import org.springframework.stereotype.Service;

@Service
public class Game implements ItemService {
    @Override
    public ApiResponse<?> create(Object createItemRequest) {
        return null;
    }

    @Override
    public ApiResponse<?> update(String id, Object updateItemRequest) {
        return null;
    }

    @Override
    public void delete(Object deleteItemRequest) {

    }

    @Override
    public ApiResponse<?> getUserItems(String itemType, String userId) {
        return null;
    }

    @Override
    public ApiResponse<?> getUserItem(String itemType, String userId, String itemId) {
        return null;
    }

    @Override
    public ItemType getItemType() {
        return ItemType.GAMES;
    }
}
