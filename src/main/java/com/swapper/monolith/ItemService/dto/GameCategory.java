package com.swapper.monolith.ItemService.dto;

public enum GameCategory {

    MAIN_GAME(0),
    DLC_ADDON(1),
    EXPANSION(2),
    BUNDLE(3),
    STANDALONE_EXPANSION(4),
    MOD(5),
    EPISODE(6),
    SEASON(7),
    REMAKE(8),
    REMASTER(9),
    EXPANDED_GAME(10),
    PORT(11),
    FORK(12),
    PACK(13),
    UPDATE(14);

    private final int value;

    GameCategory(int value) {
        this.value = value;
    }
}