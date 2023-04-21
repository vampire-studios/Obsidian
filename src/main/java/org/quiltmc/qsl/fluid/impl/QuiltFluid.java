/*
 * Copyright 2022 QuiltMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.quiltmc.qsl.fluid.impl;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.quiltmc.qsl.fluid.api.QuiltFlowableFluidExtensions;

public abstract class QuiltFluid extends FlowingFluid implements QuiltFlowableFluidExtensions {
	/**
	 * @return whether the given fluid an instance of this fluid
	 */
	@Override
	public boolean isSame(Fluid fluid) {
		return fluid == getSource() || fluid == getFlowing();
	}

	/**
	 * @return whether the fluid infinite like water
	 */
	@Override
	protected boolean canConvertToSource(Level world) {
		return false;
	}

	/**
	 * Perform actions when fluid flows into a replaceable block. Water drops
	 * the block's loot table. Lava plays the "block.lava.extinguish" sound.
	 */
	@Override
	protected void beforeDestroyingBlock(LevelAccessor world, BlockPos pos, BlockState state) {
		final BlockEntity blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
		Block.dropResources(state, world, pos, blockEntity);
	}

	/**
	 * Lava returns true if its FluidState is above a certain height and the
	 * Fluid is Water.
	 *
	 * @return whether the given Fluid can flow into this FluidState
	 */
	@Override
	protected boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockView, BlockPos blockPos, Fluid fluid, Direction direction) {
		return false;
	}

	/**
	 * The speed at which Fluids flow.
	 * Water returns 4. Lava returns 2 in the Overworld and 4 in the Nether.
	 */
	@Override
	protected int getSlopeFindDistance(LevelReader worldView) {
		return 4;
	}

	/**
	 * Water returns 1. Lava returns 2 in the Overworld and 1 in the Nether.
	 */
	@Override
	protected int getDropOff(LevelReader worldView) {
		return 1;
	}

	/**
	 * Water returns 5. Lava returns 30 in the Overworld and 10 in the Nether.
	 */
	@Override
	public int getTickDelay(LevelReader worldView) {
		return 5;
	}

	/**
	 * Water and Lava both return 100.0F.
	 */
	@Override
	protected float getExplosionResistance() {
		return 100.0F;
	}
}