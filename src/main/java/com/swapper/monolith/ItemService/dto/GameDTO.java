package com.swapper.monolith.ItemService.dto;

import com.swapper.monolith.ItemService.constants.Condition;
import com.swapper.monolith.ItemService.constants.Console;
import com.swapper.monolith.ItemService.constants.ItemStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class GameDTO {
    private String gameId;
    private String name;
    private Condition condition;
    private Console console;
    private String region;
    private BigDecimal price;
    private ItemStatus status;
    private String ownerId;
    private Date createdAt;
    private Date updatedAt;

    @Override
    public String toString() {
        return "GameDTO{" +
                "gameId='" + gameId + '\'' +
                ", name='" + name + '\'' +
                ", condition=" + condition +
                ", console=" + console +
                ", region='" + region + '\'' +
                ", price=" + price +
                ", status=" + status +
                ", ownerId='" + ownerId + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
