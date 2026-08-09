package com.swapper.monolith.VaultService.dto.constant;

public enum GamerTitle {
    NEWCOMER("Newcomer"),
    EXPLORER("Explorer"),
    STORY_HUNTER("Story Hunter"),
    COMPLETIONIST("Completionist"),
    ACHIEVEMENT_HUNTER("Achievement Hunter"),
    SPEEDRUNNER("Speedrunner"),
    MULTIPLAYER_MANIAC("Multiplayer Maniac"),
    INDIE_DISCOVERER("Indie Discoverer"),
    RPG_MASTER("RPG Master"),
    STRATEGIST("Strategist"),
    CASUAL_CHAMPION("Casual Champion"),
    VARIETY_GAMER("Variety Gamer");

    private final String displayName;

    GamerTitle(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Temporary implementation based only on total games.
     * Later you can replace this with richer logic using
     * currentlyPlaying, genres, achievements, etc.
     */
    public static GamerTitle fromGameCount(int games) {
        if (games >= 200) return RPG_MASTER;
        if (games >= 100) return COMPLETIONIST;
        if (games >= 50) return ACHIEVEMENT_HUNTER;
        if (games >= 25) return STORY_HUNTER;
        if (games >= 10) return EXPLORER;
        return NEWCOMER;
    }
}
