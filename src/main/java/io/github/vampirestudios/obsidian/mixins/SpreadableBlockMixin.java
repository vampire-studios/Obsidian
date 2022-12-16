package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.block.spread.IForgeSpreadingBlock;
import io.github.vampirestudios.obsidian.block.spread.SpreadBehaviors;
import io.github.vampirestudios.obsidian.block.spread.SpreaderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SnowyBlock;
import net.minecraft.block.SpreadableBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SpreadableBlock.class)
public abstract class SpreadableBlockMixin extends SnowyBlock implements IForgeSpreadingBlock {

	protected SpreadableBlockMixin(Settings settings) {
		super(settings);
	}

	@Shadow
	protected static boolean canSurvive(BlockState state, WorldView world, BlockPos pos) {
		return false;
	}

	@Shadow
	protected static boolean canSpread(BlockState state, WorldView world, BlockPos pos) {
		return false;
	}

	/**
	 * @author OliviaTheVampire
	 */
	@Overwrite
	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (!canSurvive(state, world, pos)) {
			if (!world.isRegionLoaded(pos.add(-1, -1, -1), pos.add(1, 1, 1))) return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
			if (SpreadBehaviors.canSpread(state, SpreaderType.REVERT)) //Forge: switch to use SpreadBehaviors API, so this class can be used more easily
				world.setBlockState(pos, SpreadBehaviors.getSpreadState(state, world, pos, SpreaderType.REVERT));
		} else {
			if (!world.isRegionLoaded(pos.add(-3, -3, -3), pos.add(3, 3, 3))) return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
			if (world.getLightLevel(pos.up()) >= 9) {
				this.spread(state, world, pos, random, 4, 1);
				if (true) return; // Forge: replace default behavior with IForgeSpreadingBlock#spread
				BlockState blockState = this.getDefaultState();

				for (int i = 0; i < 4; ++i) {
					BlockPos blockPos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
					if (world.getBlockState(blockPos).isOf(Blocks.DIRT) && canSpread(blockState, world, blockPos)) {
						world.setBlockState(blockPos, blockState.with(SNOWY, world.getBlockState(blockPos.up()).isOf(Blocks.SNOW)));
					}
				}
			}

		}
	}
}
