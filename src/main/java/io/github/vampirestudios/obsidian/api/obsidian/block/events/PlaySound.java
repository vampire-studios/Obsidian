package io.github.vampirestudios.obsidian.api.obsidian.block.events;

import io.github.vampirestudios.obsidian.api.obsidian.block.Event;
import net.minecraft.resources.ResourceLocation;

public class PlaySound extends Event {
    public ResourceLocation sound;
    public String target;
}
