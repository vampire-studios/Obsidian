package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
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

    public TagKey<Item> getTag() {
        return TagKey.of(RegistryKeys.ITEM, tag);
    }

    public enum Operation {
        ADD,
        REMOVE
    }

}
