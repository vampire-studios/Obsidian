package io.github.vampirestudios.obsidian.client;

import com.google.gson.JsonElement;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class JsonGenerator {
	private final Logger console;
	private final Map<ResourceLocation, JsonElement> map;

	public JsonGenerator(Logger c, Map<ResourceLocation, JsonElement> m) {
		console = c;
		map = m;
	}

	public void json(ResourceLocation id, JsonElement json) {
		map.put(id, json);
		console.info("Generated " + id + ": " + json);
	}
}