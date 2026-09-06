package io.github.vampirestudios.obsidian.addon_modules.nexo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import net.minecraft.resources.Identifier;

import java.io.IOException;

public class JacksonIdentifierDeserializer extends JsonDeserializer<Identifier> {

	@Override
	public Identifier deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
		JsonNode node = jsonParser.getCodec().readTree(jsonParser);
		String IdentifierString = node.asText();
		return Identifier.tryParse(IdentifierString);
	}
}