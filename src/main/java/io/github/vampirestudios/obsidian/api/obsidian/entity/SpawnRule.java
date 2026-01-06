package io.github.vampirestudios.obsidian.api.obsidian.entity;

import io.github.vampirestudios.obsidian.api.obsidian.entity.conditions.SpawnFilter;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.Identifier;

public class SpawnRule {

	public Identifier id;
	public Identifier entity;
	public String populationControl;

	public static class Condition {
		public Map<String, SpawnFilter> components = new HashMap<>();
	}

}
