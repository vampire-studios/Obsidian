package io.github.vampirestudios.obsidian.client;

import com.google.gson.JsonElement;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class JsonGenerator {
	private final Logger console;
	private final Map<Identifier, JsonElement> map;

	public JsonGenerator(Logger c, Map<Identifier, JsonElement> m) {
		console = c;
		map = m;
	}

	public void json(Identifier id, JsonElement json) {
		map.put(id, json);
		console.info("Generated " + id + ": " + json);
	}
}