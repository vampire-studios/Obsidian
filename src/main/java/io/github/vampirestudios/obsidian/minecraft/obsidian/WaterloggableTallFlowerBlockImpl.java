package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class WaterloggableTallFlowerBlockImpl extends DoublePlantBlock implements BonemealableBlock, SimpleWaterloggedBlock {
	/**
	 * The age property for the plant currently being constructed, or null when it does not grow.
	 * {@link #createBlockStateDefinition} runs from the block constructor, before this subclass can assign
	 * a field, so the property has to be handed over out-of-band.
	 */
	private static final ThreadLocal<IntegerProperty> CONSTRUCTING_AGE = new ThreadLocal<>();

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private final Block block;
	/** Null for a plant that does not grow, which is every one that declares no {@code growable}. */
	private final IntegerProperty age;

	public WaterloggableTallFlowerBlockImpl(Block block, Properties settings) {
		super(prepare(block, settings));
		this.block = block;
		this.age = CONSTRUCTING_AGE.get();
		CONSTRUCTING_AGE.remove();

		BlockState defaults = this.defaultBlockState().setValue(WATERLOGGED, false);
		this.registerDefaultState(age == null ? defaults : defaults.setValue(age, block.growable.min_age));
	}

	/** Builds the age property and stashes it for {@link #createBlockStateDefinition}. */
	private static Properties prepare(Block block, Properties settings) {
		CONSTRUCTING_AGE.set(block.growable == null ? null
				: IntegerProperty.create("age", block.growable.min_age,
						Math.max(block.growable.min_age + 1, block.growable.max_age)));
		return settings;
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

	public IntegerProperty getAgeProperty() {
		return age;
	}

	public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
		return false;
	}

	public boolean isValidBonemealTarget(LevelReader world, BlockPos pos, BlockState state, BonemealSource source) {
		return true;
	}

	public boolean isBonemealSuccess(Level world, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
		return true;
	}

	public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
		popResource(world, pos, new ItemStack(this));
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockPos blockPos = ctx.getClickedPos();
		Level world = ctx.getLevel();
		boolean bl = world.getFluidState(blockPos).getType() == Fluids.WATER;
		return blockPos.getY() < world.getMaxY() - 1 && world.getBlockState(blockPos.above()).setValue(WATERLOGGED, bl).canBeReplaced(ctx) ? super.getStateForPlacement(ctx) : null;
	}

	public int getMaxAge() {
		return block.growable.max_age;
	}

	protected int getAge(BlockState state) {
		return state.getValue(this.getAgeProperty());
	}

	public BlockState withAge(int age) {
		return this.defaultBlockState().setValue(this.getAgeProperty(), age);
	}

	public boolean isMature(BlockState state) {
		return state.getValue(this.getAgeProperty()) >= this.getMaxAge();
	}

	public boolean isRandomlyTicking(BlockState state) {
		return age != null && !this.isMature(state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(WATERLOGGED);
		IntegerProperty constructing = CONSTRUCTING_AGE.get();
		if (constructing != null) builder.add(constructing);
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		if (age == null) return;
		if (world.getRawBrightness(pos, 0) >= 9) {
			int i = this.getAge(state);
			if (i < this.getMaxAge()) {
				float f = 1.0F;
				if (random.nextInt((int) (25.0F / f) + 1) == 0) {
					world.setBlock(pos, this.withAge(i + 1), 2);
				}
			}
		}

	}

	public void applyGrowth(Level world, BlockPos pos, BlockState state) {
		int i = this.getAge(state) + this.getGrowthAmount(world);
		int j = this.getMaxAge();
		if (i > j) {
			i = j;
		}

		world.setBlock(pos, this.withAge(i), 2);
	}

	protected int getGrowthAmount(Level world) {
		return Mth.nextInt(world.getRandom(), 2, 5);
	}
}
