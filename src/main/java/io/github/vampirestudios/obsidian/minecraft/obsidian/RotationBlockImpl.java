package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.block.entity.RotationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public abstract class RotationBlockImpl extends BlockImpl implements EntityBlock {

	protected RotationBlockImpl(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Properties settings) {
		super(block, settings);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new RotationBlockEntity(pos, state);
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.INVISIBLE;
	}

	public abstract IntegerProperty rotationProperty();

	public abstract int segments();

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);

		if (!declaresPropertyNamed(rotationProperty().getName())) {
			builder.add(rotationProperty());
		}
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockState state = super.getStateForPlacement(ctx);
		if (state == null || !state.hasProperty(rotationProperty())) return state;

		int segments = segments();
		int rotation = Mth.floor(ctx.getRotation() * segments / 360.0F + 0.5F) & (segments - 1);
		return state.setValue(rotationProperty(), rotation);
	}

	@Override
	public @NonNull BlockState rotate(BlockState state, Rotation rotation) {
		if (!state.hasProperty(rotationProperty())) return super.rotate(state, rotation);
		return state.setValue(rotationProperty(), rotation.rotate(state.getValue(rotationProperty()), segments()));
	}

	@Override
	public @NonNull BlockState mirror(BlockState state, Mirror mirror) {
		if (!state.hasProperty(rotationProperty())) return super.mirror(state, mirror);
		return state.setValue(rotationProperty(), mirror.mirror(state.getValue(rotationProperty()), segments()));
	}
}
