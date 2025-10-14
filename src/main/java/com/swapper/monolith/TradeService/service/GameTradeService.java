package com.swapper.monolith.TradeService.service;

import com.swapper.monolith.TradeService.dto.Game;
import com.swapper.monolith.TradeService.dto.ItemImage;
import com.swapper.monolith.dto.*;
import com.swapper.monolith.exception.enums.ApiResponses;
import com.swapper.monolith.TradeService.dto.Order;
import com.swapper.monolith.model.User;
import com.swapper.monolith.TradeService.repository.GameTradeRepository;
import com.swapper.monolith.service.GameService;
import com.swapper.monolith.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class GameTradeService {
    GameTradeRepository gameTradeRepository;
    UserService userService;
    GameService gameService;
    public ApiResponse<Game> saveGame(CreateItemRequest createItemRequest) {
        Game newItem = Game.from(createItemRequest);
        newItem.setGameId(UUID.randomUUID().toString());
        List<ItemImage> itemImages = createItemRequest.imageUrls().stream().map(imageUrl->
                ItemImage.builder()
                        .imageUrl(imageUrl)
                        .item(newItem)
                        .build()

        ).toList();
        newItem.setImages(itemImages);

        User user = userService.findByUserId(createItemRequest.userId());
        if (user == null) {
            LoggerFactory.getLogger(GameTradeService.class).error("User with user id - " + createItemRequest.userId() + " not found");
            throw new UsernameNotFoundException("User not found");
        }
        newItem.setOwner(user);
        gameTradeRepository.save(newItem);

        ApiResponses response = ApiResponses.CREATED;
        return ApiResponse.<Game>builder()
                .status(response.getHttpStatus())
                .payload(newItem)
                .message(response.getMessage())
                .build();
    }
    public ApiResponse<Page<Game>> getUserGames(String userId,int pageSize,int pageNumber){
        Pageable page = PageRequest.of(pageNumber,pageSize, Sort.by(Sort.Direction.DESC,"createdAt"));

        User owner = userService.findByUserId(userId);
        if(owner == null) {
            LoggerFactory.getLogger(GameTradeService.class).error("User with user id - " + userId + " not found");
            throw new UsernameNotFoundException("User with user id - " + userId + " not found");
        }
        ApiResponses httpResponse = ApiResponses.OK;
        Page<Game> items = gameTradeRepository.findByOwner(owner, page);

        UserItemsResponse userItemsResponse = new UserItemsResponse();
        userItemsResponse.setItems(items.getContent());
        Page<Game> response = new PageImpl<>(userItemsResponse.getItems(),page,items.getTotalPages());

        return ApiResponse.<Page<Game>>builder()
                .payload(response)
                .message(httpResponse.getMessage())
                .status(httpResponse.getHttpStatus())
                .build();
    }

    public ApiResponse<Order> facilitateOrder(CreateOrderRequest createOrderRequest) {
        if(createOrderRequest.buyerId()==null||createOrderRequest.sellerId()==null || createOrderRequest.gameId()==null || createOrderRequest.buyerId().equals(createOrderRequest.sellerId())){
            throw new IllegalArgumentException("1. BuyerId and sellerId cannot be null\n2. Buyer cannot be same as seller\n3. Game must exist");
        }

        User buyer = userService.findByUserId(createOrderRequest.buyerId());
        User seller = userService.findByUserId(createOrderRequest.sellerId());
        Game game = gameService.getGameById(createOrderRequest.gameId());

        if(buyer==null || seller==null || game==null){
            throw new NullPointerException();
        }
        Order newOrder = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .buyer(buyer)
                .seller(seller)
                .game(game)
                .build();
        ApiResponses httpResponse = ApiResponses.CREATED;
        gameTradeRepository.save(game);
        return ApiResponse.<Order>builder()
                .payload(newOrder)
                .message(httpResponse.getMessage())
                .status(httpResponse.getHttpStatus())
                .build();
    }
}
