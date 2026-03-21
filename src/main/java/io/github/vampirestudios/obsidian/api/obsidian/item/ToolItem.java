package io.github.vampirestudios.obsidian.api.obsidian.item;

import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ToolMaterial;

import java.util.Locale;

public class ToolItem extends Item {

    public enum Type {
        @com.google.gson.annotations.SerializedName("pickaxe")   PICKAXE,
        @com.google.gson.annotations.SerializedName("shovel")    SHOVEL,
        @com.google.gson.annotations.SerializedName("hoe")       HOE,
        @com.google.gson.annotations.SerializedName("axe")       AXE,
        @com.google.gson.annotations.SerializedName("brush")     BRUSH,
        @com.google.gson.annotations.SerializedName("paxel")     PAXEL,
        @com.google.gson.annotations.SerializedName("mattock")   MATTOCK,
        @com.google.gson.annotations.SerializedName("hammer")    HAMMER,
        @com.google.gson.annotations.SerializedName("drill")     DRILL,
        @com.google.gson.annotations.SerializedName("excavator") EXCAVATOR,
        @com.google.gson.annotations.SerializedName("chisel")    CHISEL
    }

    public Object material;
    public Type tool_type;

    /** Radius of area mining for hammer/drill/excavator. 1 = 3x3, 2 = 5x5. */
    @com.google.gson.annotations.SerializedName("mining_radius")
    public int mining_radius = 1;

    /** Block conversion pairs for chisel-type tools. */
    @com.google.gson.annotations.SerializedName("chisel_mappings")
    public java.util.List<ChiselMapping> chisel_mappings;

    public static class ChiselMapping {
        /** Identifier of the block to convert from. */
        public String from;
        /** Identifier of the block to convert to. */
        public String to;
        /** Sound to play on conversion (optional). */
        public String sound;
        /** Item dropped on conversion (optional). */
        @com.google.gson.annotations.SerializedName("dropped_item")
        public String dropped_item;
        /** Whether right-clicking with the chisel on the converted block reverses it. */
        public boolean reversible = false;
        /** Item used to reverse the conversion (optional; defaults to the chisel itself). */
        @com.google.gson.annotations.SerializedName("reversal_item")
        public ConversionItem reversal_item;

        public static class ConversionItem {
            public String item;
            public String tag;
        }
    }

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