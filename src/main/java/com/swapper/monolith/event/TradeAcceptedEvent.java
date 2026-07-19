package com.swapper.monolith.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TradeAcceptedEvent {
    String tradeId;
    String initiatorUserId;
    String receiverUserId;

    public TradeAcceptedEvent(String tradeId, String initiatorUserId, String receiverUserId) {
        this.tradeId = tradeId;
        this.initiatorUserId = initiatorUserId;
        this.receiverUserId = receiverUserId;
    }
}
