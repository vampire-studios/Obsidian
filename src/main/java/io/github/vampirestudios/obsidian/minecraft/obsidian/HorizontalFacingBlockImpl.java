package io.github.vampirestudios.obsidian.minecraft.obsidian;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

public class HorizontalFacingBlockImpl extends HorizontalDirectionalBlock {

	public io.github.vampirestudios.obsidian.api.obsidian.block.Block block;
	private static final MapCodec<HorizontalDirectionalBlock> CODEC = simpleCodec(HorizontalFacingBlockImpl::new);
	public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return CODEC;
	}

	public HorizontalFacingBlockImpl(BlockBehaviour.Properties properties) {
		super(properties);
		this.block = null;
	}

	public HorizontalFacingBlockImpl(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Properties settings) {
		super(settings);
		this.block = block;
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
	}

	@Override
	@NullMarked
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
		return this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
	}

	@Override
	@NullMarked
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		VoxelShape resolved = resolveShape(state, block.information.collisionShape, block.information.shape, block.information.shapes);
		return resolved != null ? resolved : Shapes.block();
	}

	@Override
	@NullMarked
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		VoxelShape resolved = resolveShape(state, block.information.outlineShape, block.information.shape, block.information.shapes);
		return resolved != null ? resolved : Shapes.block();
	}

	@Override
	@NullMarked
	protected VoxelShape getOcclusionShape(BlockState state) {
		VoxelShape resolved = resolveShape(state, block.information.outlineShape, block.information.shape, block.information.shapes);
		return resolved != null ? resolved : Shapes.block();
	}

	private VoxelShape resolveShape(BlockState state, io.github.vampirestudios.obsidian.api.obsidian.block.BlockInformation.BoundingBox specific, float[] shorthand, float[][] shorthands) {
		if (specific != null && specific.collisionType != null) {
			return switch (specific.collisionType) {
				case FULL_BLOCK -> Shapes.block();
				case BOTTOM_SLAB -> box(0, 0, 0, 16, 8, 16);
				case TOP_SLAB -> box(0, 8, 0, 16, 16, 16);
				case CUSTOM -> {
					Direction direction = state.getValue(FACING);
					// Multi-box directional takes priority, then single-box directional,
					// then multi-box fallback, then single-box fallback
					float[][] dirShapes = getDirectionalShapes(specific, direction);
					if (dirShapes != null) yield createCompositeShape(dirShapes);
					float[] dirArr = getDirectionalShape(specific, direction);
					if (dirArr != null) yield createShape(dirArr);
					if (specific.full_shapes != null) yield createCompositeShape(specific.full_shapes);
					yield box(specific.full_shape[0], specific.full_shape[1], specific.full_shape[2],
							specific.full_shape[3], specific.full_shape[4], specific.full_shape[5]);
				}
				case NONE -> Shapes.empty();
			};
		}
		// Multi-box shorthand takes priority over single-box shorthand
		if (shorthands != null) return createCompositeShape(shorthands);
		if (shorthand != null) return box(shorthand[0], shorthand[1], shorthand[2], shorthand[3], shorthand[4], shorthand[5]);
		return null;
	}

	private float[][] getDirectionalShapes(io.github.vampirestudios.obsidian.api.obsidian.block.BlockInformation.BoundingBox specific, Direction direction) {
		return switch (direction) {
			case NORTH -> specific.north_shapes;
			case SOUTH -> specific.south_shapes;
			case EAST -> specific.east_shapes;
			case WEST -> specific.west_shapes;
			case UP -> specific.up_shapes;
			case DOWN -> specific.down_shapes;
		};
	}

	private float[] getDirectionalShape(io.github.vampirestudios.obsidian.api.obsidian.block.BlockInformation.BoundingBox specific, Direction direction) {
		return switch (direction) {
			case NORTH -> specific.north_shape;
			case SOUTH -> specific.south_shape;
			case EAST -> specific.east_shape;
			case WEST -> specific.west_shape;
			case UP -> specific.up_shape;
			case DOWN -> specific.down_shape;
		};
	}

	private VoxelShape createShape(float[] arr) {
		if (arr == null) return null;
		return box(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5]);
	}

	/** Combines multiple boxes into a single composite VoxelShape using Shapes.or(). */
	private VoxelShape createCompositeShape(float[][] boxes) {
		VoxelShape result = Shapes.empty();
		for (float[] b : boxes) {
			result = Shapes.or(result, box(b[0], b[1], b[2], b[3], b[4], b[5]));
		}
		return result;
	}
}