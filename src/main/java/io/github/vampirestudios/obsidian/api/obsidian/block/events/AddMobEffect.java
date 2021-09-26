package io.github.vampirestudios.obsidian.api.obsidian.block.events;

import io.github.vampirestudios.obsidian.api.obsidian.block.Event;
import net.minecraft.util.Identifier;

public class AddMobEffect extends Event {
    public int amplifier;
    public float duration;
    public Identifier effect;
    public String target;
}
