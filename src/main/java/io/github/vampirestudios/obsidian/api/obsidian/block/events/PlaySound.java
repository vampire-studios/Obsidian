package io.github.vampirestudios.obsidian.api.obsidian.block.events;

import io.github.vampirestudios.obsidian.api.obsidian.block.Event;
import net.minecraft.util.Identifier;

public class PlaySound extends Event {
    public Identifier sound;
    public String target;
}
