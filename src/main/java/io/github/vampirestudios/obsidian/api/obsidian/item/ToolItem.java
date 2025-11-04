package io.github.vampirestudios.obsidian.api.obsidian.item;

import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ToolMaterial;

import java.util.Locale;

public class ToolItem extends Item {

    public Object material;
    public String tool_type;

	public ToolMaterial getToolMaterial() {
		switch (material) {
			case ResourceLocation resourceLocation -> {
				if (resourceLocation.getNamespace().contains("minecraft")) {
					String path = resourceLocation.getPath().toUpperCase(Locale.ROOT);
					return switch (path) {
						case "WOOD" -> ToolMaterial.WOOD;
						case "STONE" -> ToolMaterial.STONE;
						case "IRON" -> ToolMaterial.IRON;
						case "GOLD" -> ToolMaterial.GOLD;
						case "DIAMOND" -> ToolMaterial.DIAMOND;
						case "NETHERITE" -> ToolMaterial.NETHERITE;
						default -> throw new IllegalStateException(STR."Unexpected value: \{path}");
					};
				}
				return ContentRegistries.TOOL_MATERIALS.getValue(resourceLocation);
			}
			case String s -> {
				ResourceLocation location = ResourceLocation.tryParse(s);
				if (location.getNamespace().contains("minecraft")) {
					String path = location.getPath().toUpperCase(Locale.ROOT);
					return switch (path) {
						case "WOOD" -> ToolMaterial.WOOD;
						case "STONE" -> ToolMaterial.STONE;
						case "IRON" -> ToolMaterial.IRON;
						case "GOLD" -> ToolMaterial.GOLD;
						case "DIAMOND" -> ToolMaterial.DIAMOND;
						case "NETHERITE" -> ToolMaterial.NETHERITE;
						default -> throw new IllegalStateException(STR."Unexpected value: \{path}");
					};
				}
				return ContentRegistries.TOOL_MATERIALS.getValue(location);
			}
			case null, default -> {
				System.out.printf("Tier is null for %s%n", this.information.name.id);
				return null;
			}
		}
	}

}