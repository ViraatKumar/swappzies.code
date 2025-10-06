package com.swapper.monolith.controller;

import com.swapper.monolith.dto.ApiResponse;
import com.swapper.monolith.dto.CreateItemRequest;
import com.swapper.monolith.model.Item;
import com.swapper.monolith.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/item")
@PreAuthorize("hasAnyAuthority('USER')")
public class ItemController {
    @Autowired
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Item>> save(@Valid @RequestBody CreateItemRequest createItemRequest) {
        ApiResponse<Item>  apiResponse = itemService.createItem(createItemRequest);
        return ResponseEntity.status(apiResponse.status()).body(apiResponse);
    }

}
