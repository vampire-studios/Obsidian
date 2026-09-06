package io.github.vampirestudios.obsidian.api.obsidian.item;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ToolMaterial;

import java.util.Locale;

public class ToolItem extends Item {

	public enum Type {
		@SerializedName("pickaxe") PICKAXE,
		@SerializedName("shovel") SHOVEL,
		@SerializedName("hoe") HOE,
		@SerializedName("axe") AXE,
		@SerializedName("brush") BRUSH,
		@SerializedName("paxel") PAXEL,
		@SerializedName("mattock") MATTOCK,
		@SerializedName("hammer") HAMMER,
		@SerializedName("drill") DRILL,
		@SerializedName("excavator") EXCAVATOR,
		@SerializedName("chisel") CHISEL,
		@SerializedName("fishing_rod") FISHING_ROD
	}

	/** Vanilla's fishing rod durability, used when a rod does not set its own. */
	public static final int DEFAULT_ROD_DURABILITY = 64;

	public Object material;
	public Type tool_type;

	/** Radius of area mining for hammer/drill/excavator. 1 = 3x3, 2 = 5x5. */
	@SerializedName("mining_radius")
	public int mining_radius = 1;

	/** Block conversion pairs for chisel-type tools. */
	@SerializedName("chisel_mappings")
	public java.util.List<ChiselMapping> chisel_mappings;

	public static class ChiselMapping extends io.github.vampirestudios.obsidian.api.obsidian.BlockTransformOptions {
		/**
		 * What to convert from. Either a block identifier as a string, or an object in vanilla's
		 * {@code BlockPredicate} format for tag matching, {@code any_of}, {@code all_of} and so on.
		 */
		public JsonElement from;
		/**
		 * What to convert to. Either a block identifier as a string — in which case the existing block's
		 * properties are carried over — or an object in vanilla's {@code BlockStateProvider} format for
		 * weighted, random, rotated or noise-based results.
		 */
		public JsonElement to;
		/** Sound to play on conversion (optional). */
		public String sound;
		/** Item dropped on conversion (optional). */
		@SerializedName("dropped_item")
		public String dropped_item;
		/** Whether right-clicking with the chisel on the converted block reverses it. */
		public boolean reversible = false;
		/** Item used to reverse the conversion (optional; defaults to the chisel itself). */
		@SerializedName("reversal_item")
		public ConversionItem reversal_item;

		public static class ConversionItem {
			public String item;
			public String tag;
		}
	}

	public ToolMaterial getToolMaterial() {
		// Not every tool type is made of something — a brush or a fishing rod has no material.
		if (material == null) return null;

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
						default -> throw new IllegalStateException("Unexpected value: " + path);
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
						default -> throw new IllegalStateException("Unexpected value: " + path);
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