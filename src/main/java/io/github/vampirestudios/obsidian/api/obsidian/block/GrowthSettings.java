package io.github.vampirestudios.obsidian.api.obsidian.block;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

/**
 * The parts of growing that every growing block shares, whatever shape it takes: how bright it has to be,
 * how often it advances, what bone meal does to it, and how big it is at each stage.
 *
 * <p>Both {@link Growable} — crops and the growable double plants — and {@link BushProperties} extend
 * this, so a setting added here reaches all of them at once.
 */
public class GrowthSettings {

	/** The light the block needs before it grows at all. */
	@SerializedName("min_light")
	public int minLight = 9;

	/**
	 * One in this many random ticks advances a stage. Left at {@code 0}, each block type keeps its own
	 * idea of how fast it grows — for a crop that is vanilla's farmland formula, where well-watered soil
	 * in a tended row grows faster than dry ground.
	 */
	@SerializedName("growth_chance")
	public int growthChance = 0;

	/** Fewest stages one bone meal advances. */
	@SerializedName("bonemeal_min")
	public int bonemealMin = 2;

	/** Most stages one bone meal advances. Set both to {@code 1} for a bush-like single step. */
	@SerializedName("bonemeal_max")
	public int bonemealMax = 5;

	/** The chance one bone meal does anything at all. {@code 1.0} always works, as a crop does. */
	@SerializedName("bonemeal_chance")
	public float bonemealChance = 1.0F;

	/**
	 * The block's shape at each stage, as box sets in model space — {@code [[[x1,y1,z1,x2,y2,z2], ...], ...]},
	 * one entry per age. A list shorter than the age range keeps its last entry for the rest, so one shape
	 * per visible stage is enough.
	 */
	@SerializedName("shapes_by_age")
	public float[][][] shapesByAge;

	/** How often the block advances, falling back to the block type's own rate when none was declared. */
	public int growthChance(int fallback) {
		return growthChance > 0 ? growthChance : Math.max(1, fallback);
	}

	/** How many stages one bone meal advances. */
	public int bonemealIncrease(RandomSource random) {
		int min = Math.max(1, bonemealMin);
		int max = Math.max(min, bonemealMax);
		return min == max ? min : Mth.nextInt(random, min, max);
	}

	/** Whether this bone meal does anything, before any of the block's own conditions. */
	public boolean bonemealSucceeds(RandomSource random) {
		return bonemealChance >= 1.0F || random.nextFloat() < bonemealChance;
	}
}
