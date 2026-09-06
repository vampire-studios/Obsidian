package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class FacingBlockImpl extends DirectionalBlock implements PoweredBlock {

	public io.github.vampirestudios.obsidian.api.obsidian.block.Block block;

	private final BlockVariants variants;

	@Override
	public io.github.vampirestudios.obsidian.api.obsidian.block.Block declaration() {
		return block;
	}

	public FacingBlockImpl(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Properties settings) {
		super(BlockVariants.prepare(block, settings));
		this.block = block;
		this.variants = BlockVariants.consume();
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
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
	public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
		super.stepOn(world, pos, state, entity);
	}

	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockState state = this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
		return variants.onPlacement(state, ctx, state.getValue(FACING));
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
		BlockVariants.addTo(builder);
		definePowered(builder);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		VoxelShape resolved = shapeOf(state, false);
		return resolved != null ? resolved : Shapes.block();
	}

	@Override
	public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
		VoxelShape resolved = shapeOf(state, true);
		return resolved != null ? resolved : Shapes.block();
	}

	@Override
	protected VoxelShape getOcclusionShape(BlockState state) {
		VoxelShape resolved = shapeOf(state, true);
		return resolved != null ? resolved : BlockShapeUtils.occlusionFallback(block);
	}

	@Override
	protected boolean isSignalSource(BlockState state) {
		return RedstoneLogic.isSignalSource(block);
	}

	@Override
	protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction towards) {
		return RedstoneLogic.signal(block, state, towards, variants.power(), state.getValue(FACING));
	}

	@Override
	protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction towards) {
		return RedstoneLogic.isRepeater(block) ? getSignal(state, level, pos, towards) : 0;
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock,
	                            Orientation orientation, boolean movedByPiston) {
		super.neighborChanged(state, level, pos, sourceBlock, orientation, movedByPiston);
		RedstoneLogic.onNeighbourChanged(block, level, pos, state, variants.power(), state.getValue(FACING));
		refreshPower(state, level, pos);
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		// A repeater dropped next to a live wire has to catch up to it, having missed the change.
		RedstoneLogic.onNeighbourChanged(block, level, pos, state, variants.power(), state.getValue(FACING));
		super.onPlace(state, level, pos, oldState, movedByPiston);
		if (!oldState.is(this)) refreshPower(state, level, pos);
	}

	@Override
	protected net.minecraft.world.InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
	                                                               net.minecraft.world.entity.player.Player player,
	                                                               net.minecraft.world.phys.BlockHitResult hit) {
		return useToToggle(state, level, pos, super.useWithoutItem(state, level, pos, player, hit));
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		super.tick(state, level, pos, random);
		RedstoneLogic.onScheduledTick(block, level, pos, state, variants.power(), state.getValue(FACING));
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return variants.canSurvive(state, level, pos, state.getValue(FACING)) && super.canSurvive(state, level, pos);
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos,
	                                 Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
		if (!state.canSurvive(level, pos)) return Blocks.AIR.defaultBlockState();
		return super.updateShape(state, level, tickAccess, pos, direction, neighborPos, neighborState, random);
	}

	/** The variant's shape when this state has one, otherwise the block-wide shape. */
	private VoxelShape shapeOf(BlockState state, boolean outline) {
		Direction facing = state.getValue(FACING);

		VoxelShape variantShape = variants.shape(state, outline, facing);
		if (variantShape != null) return variantShape;

		return BlockShapeUtils.resolve(outline ? block.information.outlineShape : block.information.collisionShape,
				block.information.shape, block.information.shapes, facing);
	}
}