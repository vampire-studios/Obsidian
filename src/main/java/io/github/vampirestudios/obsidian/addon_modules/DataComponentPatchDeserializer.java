package io.github.vampirestudios.obsidian.addon_modules;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.component.DataComponentPatch;

import java.lang.reflect.Type;

public class DataComponentPatchDeserializer implements JsonDeserializer<DataComponentPatch> {

    @Override
    public DataComponentPatch deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject rootNode = jsonElement.getAsJsonObject();
        return DataComponentPatch.CODEC.parse(JsonOps.INSTANCE, rootNode).getOrThrow();
    }
}
