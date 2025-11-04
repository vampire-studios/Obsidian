package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.Type;

public class ResourceLocationTypeAdapter implements JsonSerializer<ResourceLocation>, JsonDeserializer<ResourceLocation> {
    @Override
    public JsonElement serialize(ResourceLocation src, Type typeOfSrc, JsonSerializationContext context) {
        // Serialize as a simple string
        return new JsonPrimitive(src.toString());
    }

    @Override
    public ResourceLocation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // If the JSON element is a string, parse it directly
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            return ResourceLocation.parse(json.getAsString());
        }
        // If it's an object, try to extract 'namespace' and 'path'
        else if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            String namespace = obj.has("namespace") ? obj.get("namespace").getAsString() : "minecraft";
            String path = obj.has("path") ? obj.get("path").getAsString() : "";
            return ResourceLocation.fromNamespaceAndPath(namespace, path);
        }
        throw new JsonParseException(STR."Invalid JSON for ResourceLocation: \{json}");
    }
}