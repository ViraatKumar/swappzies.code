package com.swapper.monolith.TradeService;

import com.swapper.monolith.UserService.User;
import lombok.Data;

@Data
public class Trade {
    User fromUser;
    User toUser;
    Object tradeItem;
}
