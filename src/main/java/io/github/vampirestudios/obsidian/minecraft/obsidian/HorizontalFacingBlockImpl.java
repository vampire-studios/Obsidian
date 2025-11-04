package io.github.vampirestudios.obsidian.minecraft.obsidian;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import io.github.vampirestudios.obsidian.registry.properties.ListProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HorizontalFacingBlockImpl extends HorizontalDirectionalBlock {

	public io.github.vampirestudios.obsidian.api.obsidian.block.Block block;
	private static final MapCodec<HorizontalDirectionalBlock> CODEC = simpleCodec(HorizontalFacingBlockImpl::new);
	private static final Map<String, ListProperty> CUSTOM_PROPERTIES = Maps.newHashMap();

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

		registerProperties();
		registerDefaultState();
	}

	private void registerProperties() {
		if (block.information.properties != null) {
			block.information.properties.forEach((key, value) -> {
				List<String> values = Arrays.asList(value); // Ensure it's treated as a list
				ListProperty property = ListProperty.create(key, values);
				if (!CUSTOM_PROPERTIES.isEmpty()) {
					CUSTOM_PROPERTIES.put(key, property);
				}
			});
		}
	}

	private void registerDefaultState() {
		BlockState state = this.stateDefinition.any();
		if (!CUSTOM_PROPERTIES.isEmpty()) {
			for (Map.Entry<String, ListProperty> entry : CUSTOM_PROPERTIES.entrySet()) {
				ListProperty property = entry.getValue();
				String defaultValue = block.information.defaultValue.get(entry.getKey());
				if (defaultValue != null && property.getPossibleValues().contains(defaultValue)) {
					state = state.setValue(property, defaultValue);
				}
			}
		}
		this.registerDefaultState(state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
		if (!CUSTOM_PROPERTIES.isEmpty()) CUSTOM_PROPERTIES.forEach((_, property) -> builder.add(property));
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockState state = super.getStateForPlacement(ctx);
		assert state != null;
		state.setValue(FACING, ctx.getHorizontalDirection().getOpposite());
		if (!CUSTOM_PROPERTIES.isEmpty()) {
			for (Map.Entry<String, ListProperty> entry : CUSTOM_PROPERTIES.entrySet()) {
				String propertyName = entry.getKey();
				ListProperty property = entry.getValue();

				String value = resolvePlacementProperty(propertyName, ctx, property);
				if (value != null && property.getPossibleValues().contains(value)) {
					state = state.setValue(property, value);
				}
			}
		}
		return state;
	}

	// Dynamically resolve property values
	private String resolvePlacementProperty(String propertyName, BlockPlaceContext ctx, ListProperty property) {
		if (propertyName.equals("placement")) {
			Direction direction = ctx.getClickedFace();
			Direction.Axis axis = direction.getAxis();
			return axis == Direction.Axis.Y ? "floor" : "wall";
		}
		// Add more dynamic resolution logic for other properties here if needed
		return property.getPossibleValues().getFirst(); // Default to the first value
	}

	@Override
	public @NotNull String toString() {
		return block.information.name.id.toString();
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

	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		if (block.information.collisionShape != null) {
			if (block.information.collisionShape.collisionType != null) {
				return switch (block.information.collisionShape.collisionType) {
					case FULL_BLOCK -> Shapes.block();
					case BOTTOM_SLAB -> box(0, 0, 0, 16, 8.0, 16);
					case TOP_SLAB -> box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
					case CUSTOM -> {
						VoxelShape shape = createShape(block.information.collisionShape.full_shape);
						VoxelShape northShape = createShape(block.information.collisionShape.north_shape);
						VoxelShape southShape = createShape(block.information.collisionShape.south_shape);
						VoxelShape eastShape = createShape(block.information.collisionShape.east_shape);
						VoxelShape westShape = createShape(block.information.collisionShape.west_shape);
						VoxelShape upShape = createShape(block.information.collisionShape.up_shape);
						VoxelShape downShape = createShape(block.information.collisionShape.down_shape);
						Direction direction = state.getValue(FACING);
						switch (direction) {
							case NORTH -> {
								if (northShape != null) yield northShape;
								else yield shape;
							}
							case SOUTH -> {
								if (southShape != null) yield southShape;
								else yield shape;
							}
							case EAST -> {
								if (eastShape != null) yield eastShape;
								else yield shape;
							}
							case WEST -> {
								if (westShape != null) yield westShape;
								else yield shape;
							}
							case DOWN -> {
								if (downShape != null) yield downShape;
								else yield shape;
							}
							case UP -> {
								if (upShape != null) yield upShape;
								else yield shape;
							}
							default -> {
								yield shape;
							}
						}
					}
					case NONE -> Shapes.empty();
				};
			} else {
				return Shapes.block();
			}
		} else {
			return Shapes.block();
		}
	}

	@Override
	public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		if (block.information.outlineShape != null) {
			if (block.information.outlineShape.collisionType != null) {
				return switch (block.information.outlineShape.collisionType) {
					case FULL_BLOCK -> Shapes.block();
					case BOTTOM_SLAB -> box(0, 0, 0, 16, 8.0, 16);
					case TOP_SLAB -> box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
					case CUSTOM -> {
						VoxelShape shape = createShape(block.information.outlineShape.full_shape);
						VoxelShape northShape = createShape(block.information.outlineShape.north_shape);
						VoxelShape southShape = createShape(block.information.outlineShape.south_shape);
						VoxelShape eastShape = createShape(block.information.outlineShape.east_shape);
						VoxelShape westShape = createShape(block.information.outlineShape.west_shape);
						VoxelShape upShape = createShape(block.information.outlineShape.up_shape);
						VoxelShape downShape = createShape(block.information.outlineShape.down_shape);
						Direction direction = state.getValue(FACING);
						switch (direction) {
							case NORTH -> {
								if (northShape != null) yield northShape;
								else yield shape;
							}
							case SOUTH -> {
								if (southShape != null) yield southShape;
								else yield shape;
							}
							case EAST -> {
								if (eastShape != null) yield eastShape;
								else yield shape;
							}
							case WEST -> {
								if (westShape != null) yield westShape;
								else yield shape;
							}
							case DOWN -> {
								if (downShape != null) yield downShape;
								else yield shape;
							}
							case UP -> {
								if (upShape != null) yield upShape;
								else yield shape;
							}
							default -> {
								yield shape;
							}
						}
					}
					case NONE -> Shapes.empty();
				};
			} else {
				return Shapes.block();
			}
		} else {
			return Shapes.block();
		}
	}

	private VoxelShape createShape(float[] boundingBox) {
		return Block.box(boundingBox[0], boundingBox[1], boundingBox[2], boundingBox[3], boundingBox[4], boundingBox[5]);
	}
}