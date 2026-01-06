package io.github.vampirestudios.obsidian.api.obsidian.block.events;

import io.github.vampirestudios.obsidian.api.obsidian.block.Event;
import net.minecraft.resources.Identifier;

public class SetBlockAtPos extends Event {
    public Identifier block;
    public float[] position;
}
