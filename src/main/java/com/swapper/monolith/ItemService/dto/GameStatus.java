package com.swapper.monolith.ItemService.dto;

public enum GameStatus {

    RELEASED(0),
    ALPHA(2),
    BETA(3),
    EARLY_ACCESS(4),
    OFFLINE(5),
    CANCELLED(6),
    RUMORED(7),
    DELISTED(8);

    private final int value;

    GameStatus(int value) {
        this.value = value;
    }
}