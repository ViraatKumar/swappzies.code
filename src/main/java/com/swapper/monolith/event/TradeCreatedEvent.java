package com.swapper.monolith.event;

import com.swapper.monolith.TradeService.dto.CreateTradeRequest;
import com.swapper.monolith.dto.UserDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;


@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TradeCreatedEvent {
    UserDTO initiator;
    UserDTO receiver;
    CreateTradeRequest request;

    public TradeCreatedEvent(UserDTO initiator,UserDTO receiver, CreateTradeRequest request) {
        this.initiator = initiator;
        this.receiver = receiver;
        this.request = request;
    }
}
