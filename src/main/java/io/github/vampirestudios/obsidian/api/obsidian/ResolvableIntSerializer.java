package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.level.storage.loot.providers.number.ints.ResolvableInt;

import java.lang.reflect.Type;

public class ResolvableIntSerializer implements JsonSerializer<ResolvableInt>, JsonDeserializer<ResolvableInt> {

	@Override
	public ResolvableInt deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
		return ResolvableInt.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
	}

	@Override
	public JsonElement serialize(ResolvableInt value, Type type, JsonSerializationContext context) {
		return ResolvableInt.CODEC.encodeStart(JsonOps.INSTANCE, value).getOrThrow();
	}
}