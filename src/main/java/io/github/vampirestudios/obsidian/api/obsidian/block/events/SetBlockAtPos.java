package io.github.vampirestudios.obsidian.api.obsidian.block.events;

import io.github.vampirestudios.obsidian.api.obsidian.block.Event;
import net.minecraft.resources.ResourceLocation;

public class SetBlockAtPos extends Event {
    public ResourceLocation block;
    public float[] position;
}
