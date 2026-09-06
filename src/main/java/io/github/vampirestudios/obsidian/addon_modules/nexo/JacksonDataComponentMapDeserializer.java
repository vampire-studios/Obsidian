package io.github.vampirestudios.obsidian.addon_modules.nexo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;

import java.io.IOException;

/** Decodes Nexo's Components section without changing identifiers or text values. */
public class JacksonDataComponentMapDeserializer extends JsonDeserializer<DataComponentMap> {

	@Override
	public DataComponentMap deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		JsonNode node = parser.getCodec().readTree(parser);
		JsonElement element = com.google.gson.JsonParser.parseString(node.toString());
		DataResult<DataComponentPatch> result = DataComponentPatch.CODEC.parse(JsonOps.INSTANCE, element);
		DataComponentPatch patch = result.getOrThrow(error -> new IOException("Failed to parse Nexo Components: " + error));

		PatchedDataComponentMap components = new PatchedDataComponentMap(DataComponentMap.EMPTY);
		components.applyPatch(patch);
		return components;
	}
}
