package io.github.vampirestudios.obsidian.api.obsidian.block;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.Identifier;

/**
 * How a crop — or a growable double plant — grows.
 *
 * <p>The light level, growth rate, bone meal behaviour and per-stage shapes come from
 * {@link GrowthSettings}; what is here is what only a planted, harvested thing has.
 */
public class Growable extends GrowthSettings {

	public int min_age, max_age;

	/** What is planted to get the block. Without one, the block's own item is planted. */
	public Identifier seed;

	/**
	 * Whether right-clicking a fully grown crop harvests it in place — the drops are given and the crop
	 * resets to its first stage — instead of having to break and replant it.
	 */
	@SerializedName("harvest_on_interact")
	public boolean harvestOnInteract = false;
}
