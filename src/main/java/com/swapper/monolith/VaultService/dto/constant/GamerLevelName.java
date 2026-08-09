package com.swapper.monolith.VaultService.dto.constant;

public enum GamerLevelName {
    NEWCOMER("Newcomer"),
    ROOKIE("Rookie"),
    ENTHUSIAST("Enthusiast"),
    COLLECTOR("Collector"),
    VETERAN("Veteran"),
    LEGEND("Legend"),
    HALL_OF_FAMER("Hall of Famer");

    private final String displayName;

    GamerLevelName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static GamerLevelName fromGameCount(int games) {
        if (games >= 500) return HALL_OF_FAMER;
        if (games >= 250) return LEGEND;
        if (games >= 100) return VETERAN;
        if (games >= 50) return COLLECTOR;
        if (games >= 10) return ENTHUSIAST;
        if (games >= 1) return ROOKIE;
        return NEWCOMER;
    }
}