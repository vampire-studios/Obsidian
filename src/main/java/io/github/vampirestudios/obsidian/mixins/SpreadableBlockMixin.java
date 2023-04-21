package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.block.spread.IForgeSpreadingBlock;
import io.github.vampirestudios.obsidian.block.spread.SpreadBehaviors;
import io.github.vampirestudios.obsidian.block.spread.SpreaderType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SpreadingSnowyDirtBlock.class)
public abstract class SpreadableBlockMixin extends SnowyDirtBlock implements IForgeSpreadingBlock {

	protected SpreadableBlockMixin(Properties settings) {
		super(settings);
	}

	@Shadow
	protected static boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return false;
	}

	@Shadow
	protected static boolean canSpread(BlockState state, LevelReader world, BlockPos pos) {
		return false;
	}

	/**
	 * @author OliviaTheVampire
	 */
	@Overwrite
	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (!canSurvive(state, world, pos)) {
			if (!world.hasChunksAt(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
			if (SpreadBehaviors.canSpread(state, SpreaderType.REVERT)) //Forge: switch to use SpreadBehaviors API, so this class can be used more easily
				world.setBlockAndUpdate(pos, SpreadBehaviors.getSpreadState(state, world, pos, SpreaderType.REVERT));
		} else {
			if (!world.hasChunksAt(pos.offset(-3, -3, -3), pos.offset(3, 3, 3))) return; // Forge: prevent loading unloaded chunks when checking neighbor's light and spreading
			if (world.getMaxLocalRawBrightness(pos.above()) >= 9) {
				this.spread(state, world, pos, random, 4, 1);
				if (true) return; // Forge: replace default behavior with IForgeSpreadingBlock#spread
				BlockState blockState = this.defaultBlockState();

				for (int i = 0; i < 4; ++i) {
					BlockPos blockPos = pos.offset(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
					if (world.getBlockState(blockPos).is(Blocks.DIRT) && canSpread(blockState, world, blockPos)) {
						world.setBlockAndUpdate(blockPos, blockState.setValue(SNOWY, world.getBlockState(blockPos.above()).is(Blocks.SNOW)));
					}
				}
			}

		}
	}
}
