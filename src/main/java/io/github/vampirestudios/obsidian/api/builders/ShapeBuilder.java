package io.github.vampirestudios.obsidian.api.builders;

import io.github.vampirestudios.obsidian.api.obsidian.DynamicShape;
import io.github.vampirestudios.obsidian.api.parsers.ThingParser;
import net.minecraft.resources.ResourceLocation;

public class ShapeBuilder extends BaseBuilder<DynamicShape, ShapeBuilder> {
    public static ShapeBuilder begin(ThingParser<ShapeBuilder> ownerParser, ResourceLocation registryName, DynamicShape dynamicShape) {
        return new ShapeBuilder(ownerParser, registryName, dynamicShape);
    }

    private final DynamicShape dynamicShape;

    private ShapeBuilder(ThingParser<ShapeBuilder> ownerParser, ResourceLocation registryName, DynamicShape dynamicShape) {
        super(ownerParser, registryName);
        this.dynamicShape = dynamicShape;
    }

    @Override
    protected String getThingTypeDisplayName() {
        return "Voxel Shape";
    }

    @Override
    protected DynamicShape buildInternal() {
        return dynamicShape;
    }
}