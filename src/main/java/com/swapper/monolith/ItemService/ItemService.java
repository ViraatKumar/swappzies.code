package com.swapper.monolith.ItemService;

import com.swapper.monolith.TradeService.dto.enums.ItemType;
import com.swapper.monolith.dto.ApiResponse;

public interface ItemService<T,R> {
    ApiResponse<?> create (T createItemRequest);
    ApiResponse<?> update (String id,R updateItemRequest);
    void delete (T deleteItemRequest);
    ApiResponse<?> getUserItems(String itemType, String userId);
    ApiResponse<?> getUserItem(String itemType, String userId, String itemId);
    ItemType getItemType();
}
