package com.swapper.monolith.service;

import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final UserService userService;

    public OrderService(UserService userService) {
        this.userService = userService;
    }

//    public ApiResponse<Order> createOrder(CreateOrderRequest createOrderRequest){
//        ApiResponses apiResponse = ApiResponses.CREATED;
//        User seller = userService.findByUserId(createOrderRequest.buyerId());
//        return ApiResponse.<String>builder()
//                .status(apiResponse.getHttpStatus())
//                .message(apiResponse.getMessage())
//                .payload()
//                .build();
//    }
//    private Order getOrderById(String orderId){
//
//    }
}
