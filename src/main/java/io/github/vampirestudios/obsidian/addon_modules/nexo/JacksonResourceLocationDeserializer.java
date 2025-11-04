package io.github.vampirestudios.obsidian.addon_modules.nexo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class JacksonResourceLocationDeserializer extends JsonDeserializer<ResourceLocation> {

    @Override
    public ResourceLocation deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String resourceLocationString = node.asText();
        return ResourceLocation.tryParse(resourceLocationString);
    }
}