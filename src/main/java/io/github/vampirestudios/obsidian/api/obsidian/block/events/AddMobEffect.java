package io.github.vampirestudios.obsidian.api.obsidian.block.events;

import io.github.vampirestudios.obsidian.api.obsidian.block.Event;
import net.minecraft.resources.ResourceLocation;

public class AddMobEffect extends Event {
    public int amplifier;
    public float duration;
    public ResourceLocation effect;
    public String target;
}
