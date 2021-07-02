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
        return switch(operation) {
            case "add" -> Operation.ADD;
            case "remove" -> Operation.REMOVE;
            default -> /*throw new IllegalStateException("Unexpected value: " + operation)*/Operation.ADD;
        };
    }

    public Tag<Item> getTag() {
        return TagRegistry.item(tag);
    }

    public enum Operation {
        ADD,
        REMOVE
    }

}
