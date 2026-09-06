package io.github.vampirestudios.obsidian.addon_modules;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;

import java.lang.reflect.Type;

public class DataComponentMapDeserializer implements JsonDeserializer<DataComponentMap> {

	@Override
	public DataComponentMap deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		JsonObject rootNode = jsonElement.getAsJsonObject();
		DataComponentPatch patch = DataComponentPatch.CODEC.parse(JsonOps.INSTANCE, rootNode).getOrThrow();
		PatchedDataComponentMap map = new PatchedDataComponentMap(DataComponentMap.EMPTY);
		map.applyPatch(patch);
		return map;
	}
}
