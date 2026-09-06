package io.github.vampirestudios.obsidian.addon_modules.nexo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.component.DataComponentPatch;

import java.io.IOException;

public class JacksonDataComponentPatchDeserializer extends JsonDeserializer<DataComponentPatch> {

	private static final Gson GSON = new GsonBuilder()
			.disableHtmlEscaping().setPrettyPrinting().setLenient()
			.enableComplexMapKeySerialization()
			.create();

	@Override
	public DataComponentPatch deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
		// Read the entire JSON node
		JsonNode node = jsonParser.getCodec().readTree(jsonParser);
		// Convert Jackson JsonNode -> JSON string
		String jsonString = node.toString();
		// Parse into Gson JsonElement without conflicting imports
		JsonElement element = com.google.gson.JsonParser.parseString(jsonString);

		// Decode with DataComponentPatch codec
		DataResult<DataComponentPatch> result = DataComponentPatch.CODEC.parse(JsonOps.INSTANCE, element);
		return result.getOrThrow(err -> new IOException("Failed to parse DataComponentPatch: " + err));
	}
}
