package io.github.vampirestudios.obsidian.api.obsidian.block.events;

import io.github.vampirestudios.obsidian.api.obsidian.block.Event;
import net.minecraft.resources.Identifier;

public class PlayEffect extends Event {
    public Identifier effect;
    public String target;
}
