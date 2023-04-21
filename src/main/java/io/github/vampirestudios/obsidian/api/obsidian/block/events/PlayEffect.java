package io.github.vampirestudios.obsidian.api.obsidian.block.events;

import io.github.vampirestudios.obsidian.api.obsidian.block.Event;
import net.minecraft.resources.ResourceLocation;

public class PlayEffect extends Event {
    public ResourceLocation effect;
    public String target;
}
