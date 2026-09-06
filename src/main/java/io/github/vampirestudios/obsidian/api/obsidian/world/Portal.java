package io.github.vampirestudios.obsidian.api.obsidian.world;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.Identifier;

/**
 * A portal linking two dimensions: the frame that holds it, the item that lights it, and where it goes.
 *
 * <p>Obsidian does not create the dimension — that is a vanilla worldgen file in the pack's {@code data}
 * directory. What a portal definition adds is the runtime half: recognising a frame, lighting it,
 * carrying entities through, and building the portal on the far side so they can come back.
 */
public class Portal {

	public transient Identifier id;

	/**
	 * The block that fills the lit frame — a block the pack defines in {@code block/} like any other.
	 *
	 * <p>Obsidian registers no block of its own for a portal: the model, shape, light, sounds, particles
	 * and collision are all the block's, declared the way every other block is. This definition only
	 * says what the portal *does*.
	 */
	@SerializedName(value = "block", alternate = {"portal_block"})
	public Identifier block;

	/** The block the frame is built from. */
	public Identifier frame;

	/** The item that lights the frame. Without one the portal can only be placed by other means. */
	public Identifier ignition;

	/** The dimension the portal leads to, e.g. {@code examplepack:cheese_dimension}. */
	public Identifier dimension;

	/** Where a portal in {@link #dimension} leads back to. */
	@SerializedName("return_dimension")
	public Identifier returnDimension = Identifier.withDefaultNamespace("overworld");

	/**
	 * How coordinates are scaled travelling **to** {@link #dimension}, and inverted coming back. The
	 * Nether uses {@code 0.125}; {@code 1.0} keeps the two dimensions aligned.
	 */
	public double scale = 1.0;

	/** Ticks an entity must stand in the portal before it travels. */
	public int delay = 80;

	/** Whether standing in the portal applies vanilla's nausea-and-wobble effect. */
	public boolean confusion = true;

	/** Played when a frame is lit. */
	public Identifier sound;

	@SerializedName("min_width")
	public int minWidth = 2;
	@SerializedName("max_width")
	public int maxWidth = 21;
	@SerializedName("min_height")
	public int minHeight = 3;
	@SerializedName("max_height")
	public int maxHeight = 21;

	public int minWidth() {
		return Math.max(1, minWidth);
	}

	public int maxWidth() {
		return Math.max(minWidth(), maxWidth);
	}

	public int minHeight() {
		return Math.max(1, minHeight);
	}

	public int maxHeight() {
		return Math.max(minHeight(), maxHeight);
	}

}
