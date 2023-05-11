package io.github.vampirestudios.obsidian.api.parsers;

import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.api.builders.BaseBuilder;
import io.github.vampirestudios.obsidian.api.builders.ShapeBuilder;
import io.github.vampirestudios.obsidian.api.obsidian.DynamicShape;
import io.github.vampirestudios.obsidian.registry.Registries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

public class ShapeParser extends ThingParser<ShapeBuilder> {
    public ShapeParser() {
        super(GSON, "shape");
    }

    @Override
    protected void finishLoadingInternal() {
        processAndConsumeErrors(getThingType(), getBuilders(), thing -> Registry.register(Registries.DYNAMIC_SHAPES, thing.getRegistryName(), thing.get()), BaseBuilder::getRegistryName);
    }

    @Override
    protected ShapeBuilder processThing(ResourceLocation key, JsonObject data, Consumer<ShapeBuilder> builderModification) {
        ShapeBuilder builder = ShapeBuilder.begin(this, key, DynamicShape.fromJson(data, null, name -> Registries.PROPERTIES.get(new ResourceLocation(name))));

        builderModification.accept(builder);

        return builder;
    }
}