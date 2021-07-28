package io.github.vampirestudios.obsidian.api.obsidian.block.events;

import io.github.vampirestudios.obsidian.api.obsidian.block.Event;

public class Damage extends Event {
    public int amount;
    public String target;
    public String type;
}
