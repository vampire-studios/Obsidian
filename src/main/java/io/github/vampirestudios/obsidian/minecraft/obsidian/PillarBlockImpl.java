package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;

public class PillarBlockImpl extends RotatedPillarBlock implements PoweredBlock {

	public io.github.vampirestudios.obsidian.api.obsidian.block.Block block;

	public PillarBlockImpl(Block block, Properties settings) {
		super(settings);
		this.block = block;
	}

	@Override
	public Block declaration() {
		return block;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		definePowered(builder);
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.block.Block sourceBlock,
	                            Orientation orientation, boolean movedByPiston) {
		super.neighborChanged(state, level, pos, sourceBlock, orientation, movedByPiston);
		refreshPower(state, level, pos);
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		super.onPlace(state, level, pos, oldState, movedByPiston);
		if (!oldState.is(this)) refreshPower(state, level, pos);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		return useToToggle(state, level, pos, super.useWithoutItem(state, level, pos, player, hit));
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
		return block.information.getBlockSettings() != null ? !block.information.getBlockSettings().translucent ? 0.2F : 1.0F : super.getShadeBrightness(state, world, pos);
	}

	@Override
	public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter world, BlockPos pos) {
		return block.information.getBlockSettings() != null ? !block.information.getBlockSettings().translucent : super.isCollisionShapeFullBlock(state, world, pos);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state) {
		return block.information.getBlockSettings() != null ? block.information.getBlockSettings().translucent : super.propagatesSkylightDown(state);
	}
}