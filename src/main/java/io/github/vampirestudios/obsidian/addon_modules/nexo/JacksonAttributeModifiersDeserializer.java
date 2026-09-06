package io.github.vampirestudios.obsidian.addon_modules.nexo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vampirestudios.obsidian.api.nexo.NexoItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Accepts both Nexo's named modern modifier map and its legacy modifier list. */
public class JacksonAttributeModifiersDeserializer extends JsonDeserializer<NexoItem.Mechanics.Attributes> {

	@Override
	public NexoItem.Mechanics.Attributes deserialize(JsonParser parser, DeserializationContext context) throws IOException {
		ObjectMapper mapper = (ObjectMapper) parser.getCodec();
		JsonNode root = mapper.readTree(parser);
		JsonNode modifiers = root.has("modifiers") ? root.get("modifiers") : root;
		List<NexoItem.Mechanics.Attributes.AttributeModifierData> parsed = new ArrayList<>();

		if (modifiers.isArray()) {
			for (JsonNode modifier : modifiers) parsed.add(read(mapper, modifier));
		} else if (modifiers.isObject()) {
			for (Map.Entry<String, JsonNode> field : modifiers.properties()) {
				if (field.getValue().isObject()) parsed.add(read(mapper, field.getValue()));
			}
		}

		NexoItem.Mechanics.Attributes attributes = new NexoItem.Mechanics.Attributes();
		attributes.modifiers = parsed;
		return attributes;
	}

	private static NexoItem.Mechanics.Attributes.AttributeModifierData read(ObjectMapper mapper, JsonNode node)
			throws IOException {
		return mapper.treeToValue(node, NexoItem.Mechanics.Attributes.AttributeModifierData.class);
	}
}
