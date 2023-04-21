package org.quiltmc.qsl.fluid.api;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;

public class QuiltFluidBlock extends LiquidBlock {

	/**
	 * Utility class, to not deal with anonymous classes.
	 */
	public QuiltFluidBlock(FlowingFluid flowableFluid, Properties settings) {
		super(flowableFluid, settings);
	}
}