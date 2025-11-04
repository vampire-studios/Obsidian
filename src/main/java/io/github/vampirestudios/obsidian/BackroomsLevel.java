package io.github.vampirestudios.obsidian;

import net.minecraft.util.StringRepresentable;

public enum BackroomsLevel implements StringRepresentable {
    LEVEL_0("level_0", "The Lobby"),
    LEVEL_1("level_1", "Habitable Zone"),
    LEVEL_2("level_2", "Pipe Dreams"),
    LEVEL_3("level_3", "Electrical Station"),
    LEVEL_4("level_4", "Abandoned Office"),
    POOL_ROOMS("pool_rooms", "Pool Rooms"),
    TERROR_HOTEL("terror_hotel", "Terror Hotel"),
    SUBURBS("suburbs", "The Suburbs"),
    PARADISE("paradise", "Paradise");

    private final String name;
    private final String description;

    BackroomsLevel(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }
}
