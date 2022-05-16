package io.github.vampirestudios.obsidian;

import net.minecraft.world.biome.source.util.MultiNoiseUtil;

public class FabricBiomes {
	public static final MultiNoiseUtil.ParameterRange FULL_RANGE = MultiNoiseUtil.ParameterRange.of(-1.0F, 1.0F);
	public static final MultiNoiseUtil.ParameterRange FROZEN_RANGE = MultiNoiseUtil.ParameterRange.of(-1.0F, -0.45F);
	public static final MultiNoiseUtil.ParameterRange UNFROZEN_RANGE = MultiNoiseUtil.ParameterRange.combine(MultiNoiseUtil.ParameterRange.of(-0.45F, -0.15F), MultiNoiseUtil.ParameterRange.of(0.55F, 1.0F));
}