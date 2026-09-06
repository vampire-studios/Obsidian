package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.level.storage.loot.providers.number.floats.ResolvableFloat;

import java.lang.reflect.Type;

public class ResolvableFloatSerializer implements JsonSerializer<ResolvableFloat>, JsonDeserializer<ResolvableFloat> {

	@Override
	public ResolvableFloat deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		return ResolvableFloat.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
	}

	@Override
	public JsonElement serialize(ResolvableFloat value, Type type, JsonSerializationContext context) {
		return ResolvableFloat.CODEC.encodeStart(JsonOps.INSTANCE, value).getOrThrow();
	}
}