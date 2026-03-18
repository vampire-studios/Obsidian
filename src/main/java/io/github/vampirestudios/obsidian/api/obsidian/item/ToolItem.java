package io.github.vampirestudios.obsidian.api.obsidian.item;

import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ToolMaterial;

import java.util.Locale;

public class ToolItem extends Item {

    public enum Type {
        @com.google.gson.annotations.SerializedName("pickaxe") PICKAXE,
        @com.google.gson.annotations.SerializedName("shovel")  SHOVEL,
        @com.google.gson.annotations.SerializedName("hoe")     HOE,
        @com.google.gson.annotations.SerializedName("axe")     AXE
    }

    public Object material;
    public Type tool_type;

	public ToolMaterial getToolMaterial() {
		switch (material) {
			case Identifier Identifier -> {
				if (Identifier.getNamespace().contains("minecraft")) {
					String path = Identifier.getPath().toUpperCase(Locale.ROOT);
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
				return ContentRegistries.TOOL_MATERIALS.getValue(Identifier);
			}
			case String s -> {
				Identifier location = Identifier.tryParse(s);
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
				System.out.printf("Tier is null for %s%n", this.information.id);
				return null;
			}
		}
	}

}