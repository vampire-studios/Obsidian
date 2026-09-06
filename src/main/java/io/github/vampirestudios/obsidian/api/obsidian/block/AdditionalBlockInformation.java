package io.github.vampirestudios.obsidian.api.obsidian.block;

import net.minecraft.resources.Identifier;

public class AdditionalBlockInformation {

	public boolean overworldLike = true;
	public boolean netherLike = false;
	public boolean bambooLike = false;
	public boolean pillarModel = false;
	public boolean topBottomModel = false;

	public String extraBlocksName = "";

	/**
	 * The companion blocks, named one by one, each either {@code true} for the defaults or an object
	 * saying how that one differs. See {@link CompanionBlocks}. The flags below are the same thing said
	 * shorter; a variant named here wins over its flag.
	 */
	public java.util.Map<String, com.google.gson.JsonElement> variants;

	public boolean slab = false;
	public boolean stairs = false;

	public boolean walls = false;
	public boolean fence = false;
	public boolean fenceGate = false;

	public boolean button = false;
	public boolean pressurePlate = false;

	public boolean door = false;
	public boolean trapdoor = false;

	public boolean path = false;
	public boolean lantern = false;
	public boolean barrel = false;
	public boolean leaves = false;
	public boolean plant = false;
	public boolean chains = false;
	public boolean cake_like = false;
	public boolean waterloggable = false;

	public boolean dyable = false;
	public int defaultColor = 16579836;

	public boolean sittable = false;
	/** Declaring this at all opts the block in; there is no separate enable flag. */
	public Convertible convertible;

	public static class Convertible extends io.github.vampirestudios.obsidian.api.obsidian.BlockTransformOptions {
		public boolean drops_item = false;
		public boolean reversible = false;

		public Identifier parent_block;
		public Identifier transformed_block;
		public Identifier dropped_item;
		public Identifier sound;

		@com.google.gson.annotations.SerializedName("conversion_item")
		public ConversionItem conversionItem;
		@com.google.gson.annotations.SerializedName("reversal_item")
		public ConversionItem reversalItem;

		public static class ConversionItem {
			public Identifier item;
			public Identifier tag;
		}

	}

}