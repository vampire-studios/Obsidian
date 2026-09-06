package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.core.BlockPos;
import io.github.vampirestudios.obsidian.api.EventActionHandler;
import io.github.vampirestudios.obsidian.api.ISeatProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class HorizontalFacingBlockImpl extends HorizontalDirectionalBlock implements ISeatProvider {

	public io.github.vampirestudios.obsidian.api.obsidian.block.Block block;
	public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

	private final @Nullable BlockVariants variants;

	public HorizontalFacingBlockImpl(BlockBehaviour.Properties properties) {
		super(properties);
		this.block = null;
		this.variants = null;
	}

	public HorizontalFacingBlockImpl(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Properties settings) {
		super(BlockVariants.prepare(block, settings));
		this.block = block;
		this.variants = BlockVariants.consume();
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public List<io.github.vampirestudios.obsidian.api.obsidian.block.Block.Behaviour.Seat> getSeats(BlockState state) {
		List<io.github.vampirestudios.obsidian.api.obsidian.block.Block.Behaviour.Seat> seats = SeatLogic.seatsOf(this.block);
		return seats != null ? seats : List.of();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING);
		BlockVariants.addTo(builder);
	}

	@Override
	@NullMarked
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
		BlockState state = this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
		return variants == null ? state : variants.onPlacement(state, blockPlaceContext, state.getValue(FACING));
	}

	@Override
	@NullMarked
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		if (variants != null) variants.afterPlace(level, pos, state, state.getValue(FACING));
	}

	@Override
	@NullMarked
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		// The lock comes first: a locked block runs no events and offers no seat until it is opened.
		InteractionResult locked = LockLogic.tryUnlock(level, pos, state, player, InteractionHand.MAIN_HAND, block, variants);
		if (locked != InteractionResult.PASS) return locked;

		fire(level, pos, player, state, "on_interact");

		InteractionResult seated = SeatLogic.sit(level, pos, state, player, hit);
		if (seated != InteractionResult.PASS) return seated;

		return super.useWithoutItem(state, level, pos, player, hit);
	}

	@Override
	@NullMarked
	protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
		fire(level, pos, player, state, "on_attack");
		super.attack(state, level, pos, player);
	}

	@Override
	@NullMarked
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		fire(level, pos, entity instanceof Player player ? player : null, state, "on_step_on");
		super.stepOn(level, pos, state, entity);
	}

	@Override
	@NullMarked
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean bl) {
		fire(level, pos, entity instanceof Player player ? player : null, state, "on_entity_inside");
		super.entityInside(state, level, pos, entity, applier, bl);
	}

	@Override
	@NullMarked
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!state.is(oldState.getBlock())) fire(level, pos, null, state, "on_place");
		// A repeater dropped next to a live wire has to catch up to it, having missed the change.
		if (block != null) {
			RedstoneLogic.onNeighbourChanged(block, level, pos, state, power(), state.getValue(FACING));
		}
		super.onPlace(state, level, pos, oldState, movedByPiston);
	}

	@Override
	@NullMarked
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
		fire(level, pos, null, state, "on_remove");
		SeatLogic.clearSeats(level, pos);
		if (variants != null) variants.afterRemove(level, pos, state, state.getValue(FACING));
		super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
	}

	@Override
	@NullMarked
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		fire(level, pos, null, state, "on_random_tick");
		super.randomTick(state, level, pos, random);
	}

	/** Passes the block's facing along so positional actions rotate with it. */
	private void fire(Level level, BlockPos pos, @Nullable Player player, BlockState state, String event) {
		EventActionHandler.handleBlockEvent(level, pos, player, block, event, state.getValue(FACING));
	}

	@Override
	@NullMarked
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		VoxelShape resolved = shapeOf(state, false);
		return resolved != null ? resolved : Shapes.block();
	}

	@Override
	@NullMarked
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		VoxelShape resolved = shapeOf(state, true);
		return resolved != null ? resolved : Shapes.block();
	}

	@Override
	@NullMarked
	protected VoxelShape getOcclusionShape(BlockState state) {
		VoxelShape resolved = shapeOf(state, true);
		if (resolved != null) return resolved;
		return BlockShapeUtils.rendersAsPlainCube(block) ? Shapes.block() : Shapes.empty();
	}

	@Override
	protected boolean isSignalSource(BlockState state) {
		return block != null && RedstoneLogic.isSignalSource(block);
	}

	@Override
	@NullMarked
	protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction towards) {
		if (block == null) return 0;
		return RedstoneLogic.signal(block, state, towards, power(), state.getValue(FACING));
	}

	@Override
	@NullMarked
	protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction towards) {
		return block != null && RedstoneLogic.isRepeater(block) ? getSignal(state, level, pos, towards) : 0;
	}

	@Override
	@NullMarked
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock,
	                            Orientation orientation, boolean movedByPiston) {
		super.neighborChanged(state, level, pos, sourceBlock, orientation, movedByPiston);
		if (block != null) {
			RedstoneLogic.onNeighbourChanged(block, level, pos, state, power(), state.getValue(FACING));
		}
	}

	@Override
	@NullMarked
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		super.tick(state, level, pos, random);
		if (block != null) {
			RedstoneLogic.onScheduledTick(block, level, pos, state, power(), state.getValue(FACING));
		}
	}

	private @Nullable IntegerProperty power() {
		return variants != null ? variants.power() : null;
	}

	@Override
	@NullMarked
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		if (variants != null && !variants.canSurvive(state, level, pos, state.getValue(FACING))) return false;
		return super.canSurvive(state, level, pos);
	}

	@Override
	@NullMarked
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos,
	                                 Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
		if (!state.canSurvive(level, pos)) return Blocks.AIR.defaultBlockState();
		return super.updateShape(state, level, tickAccess, pos, direction, neighborPos, neighborState, random);
	}

	/** The variant's shape when this state has one, otherwise the block-wide shape. */
	private @Nullable VoxelShape shapeOf(BlockState state, boolean outline) {
		Direction facing = state.getValue(FACING);

		if (variants != null) {
			VoxelShape variantShape = variants.shape(state, outline, facing);
			if (variantShape != null) return variantShape;
		}

		return BlockShapeUtils.resolve(outline ? block.information.outlineShape : block.information.collisionShape,
				block.information.shape, block.information.shapes, facing);
	}

}
