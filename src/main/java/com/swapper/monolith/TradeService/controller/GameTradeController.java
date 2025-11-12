package com.swapper.monolith.TradeService.controller;

import com.swapper.monolith.TradeService.dto.Game;
import com.swapper.monolith.TradeService.service.GameTradeService;
import com.swapper.monolith.dto.ApiResponse;
import com.swapper.monolith.dto.CreateItemRequest;
import com.swapper.monolith.dto.CreateOrderRequest;
import com.swapper.monolith.TradeService.dto.Order;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/item")
@PreAuthorize("hasAnyAuthority('USER')")
public class GameTradeController {
    @Autowired
    private final GameTradeService gameTradeService;

    public GameTradeController(GameTradeService gameTradeService) {
        this.gameTradeService = gameTradeService;
    }


    @PostMapping
    public ResponseEntity<ApiResponse<Game>> saveGame(@Valid @RequestBody CreateItemRequest createItemRequest) {
        ApiResponse<Game>  apiResponse = gameTradeService.saveGame(createItemRequest);
        return ResponseEntity.status(apiResponse.status()).body(apiResponse);
    }
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Game>>> getUserGames(@RequestParam String userId) {
        ApiResponse<Page<Game>> apiResponse = gameTradeService.getUserGames(userId,10,0);
        return ResponseEntity.status(apiResponse.status()).body(apiResponse);
    }
    @PostMapping("/order")
    public ResponseEntity<ApiResponse<Order>> order(@Valid @RequestBody CreateOrderRequest createOrderRequest) {
        ApiResponse<Order> response = gameTradeService.facilitateOrder(createOrderRequest);
        return ResponseEntity.status(response.status()).body(response);
    }

}
