package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jspecify.annotations.Nullable;

@SuppressWarnings("unused")
public class TripleCeilingPlantBlock extends VegetationBlock {
	public static final EnumProperty<TripleBlockPart> PART = CProperties.TRIPLE_BLOCK_PART;

	public TripleCeilingPlantBlock(Properties settings) {
		super(settings.offsetType(OffsetType.XZ));
		this.registerDefaultState(this.stateDefinition.any().setValue(PART, TripleBlockPart.UPPER));
	}

	protected boolean canPlantBelow(BlockState state, BlockGetter world, BlockPos pos) {
		return this.mayPlaceOn(state, world, pos);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(PART);
	}

    /*@Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState newState, LevelAccessor world, BlockPos pos, BlockPos posFrom) {
        TripleBlockPart doubleBlockHalf = state.getValue(PART);
        if (direction.getAxis() == Direction.Axis.Y && doubleBlockHalf == TripleBlockPart.LOWER == (direction == Direction.UP) && (!newState.is(this) || newState.getValue(PART) == doubleBlockHalf)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            return doubleBlockHalf == TripleBlockPart.LOWER && direction == Direction.DOWN && !state.canSurvive(world, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, newState, world, pos, posFrom);
        }
    }*/

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockPos blockPos = ctx.getClickedPos();
		return blockPos.getY() > 0 && ctx.getLevel().getBlockState(blockPos.below()).canBeReplaced(ctx) ? super.getStateForPlacement(ctx) : null;
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		world.setBlock(pos.below(1), this.defaultBlockState().setValue(PART, TripleBlockPart.MIDDLE), 3);
		world.setBlock(pos.below(2), this.defaultBlockState().setValue(PART, TripleBlockPart.LOWER), 3);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		if (state.getValue(PART) == TripleBlockPart.UPPER) {
			BlockPos blockPos = pos.above();
			return this.canPlantBelow(world.getBlockState(blockPos), world, pos);
		} else {
			BlockState blockState = world.getBlockState(pos.above());
			return blockState.is(this) && blockState.getValue(PART) == TripleBlockPart.UPPER;
		}
	}

	@Override
	public void playerDestroy(ServerLevel level, ServerPlayer player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack destroyedWith) {
		super.playerDestroy(level, player, pos, Blocks.AIR.defaultBlockState(), blockEntity, destroyedWith);
	}

	@Override
	public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
		if (!world.isClientSide()) {
			if (player.isCreative()) {
				TripleCeilingPlantBlock.onBreakInCreative(world, pos, state, player);
			} else {
				TripleCeilingPlantBlock.dropResources(state, world, pos, null, player, player.getMainHandItem());
			}
		}

		return super.playerWillDestroy(world, pos, state, player);
	}

	protected static void onBreakInCreative(Level world, BlockPos pos, BlockState state, Player player) {
		TripleBlockPart doubleBlockHalf = state.getValue(PART);
		if (doubleBlockHalf == TripleBlockPart.UPPER) {
			BlockPos blockPos = pos.above();
			BlockState blockState = world.getBlockState(blockPos);
			if (blockState.getBlock() == state.getBlock() && blockState.getValue(PART) == TripleBlockPart.LOWER) {
				world.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 35);
				world.levelEvent(player, 2001, blockPos, Block.getId(blockState));
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	@Environment(EnvType.CLIENT)
	public long getSeed(BlockState state, BlockPos pos) {
		return Mth.getSeed(pos.getX(), pos.below(state.getValue(PART) == TripleBlockPart.LOWER ? 0 : 1).getY(), pos.getZ());
	}
}