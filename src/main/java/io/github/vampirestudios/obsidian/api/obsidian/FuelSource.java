package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class FuelSource {

    public String operation;
    public ResourceLocation item;
    public ResourceLocation tag;
    public int burn_time = -1;

    public Operation getOperation() {
        return switch(operation) {
            case "add" -> Operation.ADD;
            case "remove" -> Operation.REMOVE;
            default -> /*throw new IllegalStateException("Unexpected value: " + operation)*/Operation.ADD;
        };
    }

    public TagKey<Item> getTag() {
        return TagKey.create(Registries.ITEM, tag);
    }

    public enum Operation {
        ADD,
        REMOVE
    }

}
