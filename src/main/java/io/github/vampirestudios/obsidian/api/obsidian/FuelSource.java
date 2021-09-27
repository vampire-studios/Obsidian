package io.github.vampirestudios.obsidian.api.obsidian;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

public class FuelSource {

    public String operation;
    public Identifier item;
    public Identifier tag;
    public int burn_time = -1;

    public Operation getOperation() {
        if ("remove".equals(operation)) {
            return Operation.REMOVE;
        }
        return Operation.ADD;
    }

    public Tag<Item> getTag() {
        return TagRegistry.item(tag);
    }

    public enum Operation {
        ADD,
        REMOVE
    }

}
