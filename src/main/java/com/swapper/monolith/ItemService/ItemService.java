package com.swapper.monolith.ItemService;

import com.swapper.monolith.TradeService.dto.enums.ItemType;

import java.util.List;

public interface ItemService<T> {
    T create(T createRequest);
    T update(String id, T updateRequest);
    void delete(String id);
    List<T> getUserItems(String userId);
    T getUserItem(String userId, String itemId);
    ItemType getItemType();
}
