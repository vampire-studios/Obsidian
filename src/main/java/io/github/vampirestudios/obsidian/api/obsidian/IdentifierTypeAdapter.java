package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.*;
import net.minecraft.resources.Identifier;

import java.lang.reflect.Type;

public class IdentifierTypeAdapter implements JsonSerializer<Identifier>, JsonDeserializer<Identifier> {
    @Override
    public JsonElement serialize(Identifier src, Type typeOfSrc, JsonSerializationContext context) {
        // Serialize as a simple string
        return new JsonPrimitive(src.toString());
    }

    @Override
    public Identifier deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // If the JSON element is a string, parse it directly
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            return Identifier.parse(json.getAsString());
        }
        // If it's an object, try to extract 'namespace' and 'path'
        else if (json.isJsonObject()) {
            JsonObject obj = json.getAsJsonObject();
            String namespace = obj.has("namespace") ? obj.get("namespace").getAsString() : "minecraft";
            String path = obj.has("path") ? obj.get("path").getAsString() : "";
            return Identifier.fromNamespaceAndPath(namespace, path);
        }
        throw new JsonParseException("Invalid JSON for Identifier: " + json);
    }
}