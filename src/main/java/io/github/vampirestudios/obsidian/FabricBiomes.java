package io.github.vampirestudios.obsidian;

import net.minecraft.world.level.biome.Climate;

public class FabricBiomes {
	public static final Climate.Parameter FULL_RANGE = Climate.Parameter.span(-1.0F, 1.0F);
	public static final Climate.Parameter FROZEN_RANGE = Climate.Parameter.span(-1.0F, -0.45F);
	public static final Climate.Parameter UNFROZEN_RANGE = Climate.Parameter.span(Climate.Parameter.span(-0.45F, -0.15F), Climate.Parameter.span(0.55F, 1.0F));
}