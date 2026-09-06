package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.EventActionHandler;
import io.github.vampirestudios.obsidian.api.obsidian.block.BushProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.VegetationBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

/**
 * A berry bush, the shape vanilla's sweet berry bush has: it grows through a few stages, is picked by
 * hand once ripe rather than broken, drops back a stage when it is, and scratches whatever pushes
 * through it.
 *
 * <p>Everything about it is read from the declaration — {@code bush_properties} for picking and
 * scratching, the growth settings it inherits for how fast it grows and what bone meal does — so
 * nothing here is fixed to vanilla's numbers except the defaults those settings carry.
 *
 * @see BushProperties
 */
public class BushBlockImpl extends VegetationBlock implements BonemealableBlock, SimpleWaterloggedBlock {

	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	/**
	 * The state properties for the bush currently being constructed. {@link #createBlockStateDefinition}
	 * runs from the block constructor, before this subclass can assign a field, so which age property the
	 * bush uses — and whether it waterlogs at all — has to be handed over out-of-band, the same trick
	 * {@link BlockImpl} plays for declared properties.
	 */
	private static final ThreadLocal<Shape> CONSTRUCTING = ThreadLocal.withInitial(
			() -> new Shape(new BushProperties().ageProperty(), false));

	/** A bare bush sits low; one with any growth on it fills its block, as vanilla's does. */
	private static final float[][] BARE = {{3.0F, 0.0F, 3.0F, 13.0F, 8.0F, 13.0F}};
	private static final float[][] GROWN = {{1.0F, 0.0F, 1.0F, 15.0F, 16.0F, 15.0F}};

	private record Shape(IntegerProperty age, boolean waterloggable) {
	}

	private final io.github.vampirestudios.obsidian.api.obsidian.block.Block block;
	private final BushProperties bush;
	private final IntegerProperty age;
	private final boolean waterloggable;
	private final int maxAge;

	public BushBlockImpl(io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
	                     BlockBehaviour.Properties settings) {
		super(prepare(block, settings));
		this.block = block;
		this.bush = BushProperties.of(block);

		Shape shape = CONSTRUCTING.get();
		CONSTRUCTING.remove();
		this.age = shape.age();
		this.waterloggable = shape.waterloggable();
		this.maxAge = bush.resolvedMaxAge();

		BlockState defaults = this.stateDefinition.any().setValue(this.age, 0);
		this.registerDefaultState(waterloggable ? defaults.setValue(WATERLOGGED, false) : defaults);
	}

	/** Stashes the state properties for {@link #createBlockStateDefinition}, which runs before the constructor body. */
	private static BlockBehaviour.Properties prepare(io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
	                                                 BlockBehaviour.Properties settings) {
		boolean waterloggable = block != null && block.additional_information != null
				&& block.additional_information.waterloggable;
		CONSTRUCTING.set(new Shape(BushProperties.of(block).ageProperty(), waterloggable));
		return settings;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
		Shape shape = CONSTRUCTING.get();
		builder.add(shape.age());
		if (shape.waterloggable()) builder.add(WATERLOGGED);
	}

	/* ------------------------------------------------------------------------------------------ */

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		int current = state.getValue(age);

		VoxelShape declared = BlockShapeUtils.shapeForAge(bush.shapesByAge, current);
		if (declared != null) return declared;

		// Whatever the block itself declared, when it named a shape but not one per stage.
		VoxelShape blockWide = block == null || block.information == null ? null
				: BlockShapeUtils.resolve(block.information.collisionShape,
						block.information.shape, block.information.shapes, null);
		if (blockWide != null) return blockWide;

		return BlockShapeUtils.boxes(current == 0 ? BARE : GROWN);
	}

	@Override
	protected boolean mayPlaceOn(BlockState floor, BlockGetter level, BlockPos pos) {
		if (block == null || block.can_plant_on == null || block.can_plant_on.isEmpty()) {
			return super.mayPlaceOn(floor, level, pos);
		}
		return block.getSupportableBlocks().contains(floor.getBlock());
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		EventActionHandler.handleBlockEvent(level, pos, null, block, "on_random_tick");

		int current = state.getValue(age);
		if (current >= maxAge) return;
		if (level.getRawBrightness(pos, 0) < bush.minLight) return;
		if (random.nextInt(bush.growthChance(5)) != 0) return;

		level.setBlock(pos, state.setValue(age, current + 1), 2);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		EventActionHandler.handleBlockEvent(level, player, block, "on_interact", hit);

		int current = state.getValue(age);
		// A bush that is not ripe yet is left alone, so it can still be broken or built against as usual.
		if (current < bush.resolvedRipeAge()) return super.useWithoutItem(state, level, pos, player, hit);

		if (!level.isClientSide()) {
			int count = bush.berriesAt(current, level.getRandom());
			if (count > 0) popResource(level, pos, new ItemStack(berry(), count));

			SoundEvent sound = bush.getPickSound();
			if (sound != null) {
				level.playSound(null, pos, sound, SoundSource.BLOCKS,
						1.0F, 0.8F + level.getRandom().nextFloat() * 0.4F);
			}
			level.setBlock(pos, state.setValue(age, bush.resolvedPickedAge()), 2);
		}
		return InteractionResult.SUCCESS;
	}

	/** What picking gives: the declared berry, or the bush's own item when it named none. */
	private Item berry() {
		if (bush.berry != null) {
			Item declared = BuiltInRegistries.ITEM.getValue(bush.berry);
			if (declared != null) return declared;
		}
		return this.asItem();
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity,
	                            InsideBlockEffectApplier applier, boolean bl) {
		EventActionHandler.handleBlockEvent(level, pos, entity instanceof Player player ? player : null,
				block, "on_entity_inside");
		super.entityInside(state, level, pos, entity, applier, bl);

		if (state.getValue(age) == 0) return;
		if (!(entity instanceof LivingEntity)) return;
		// The mobs that live in a bush are not slowed or hurt by it — vanilla's foxes and bees by default.
		if (bush.isImmune(entity.getType())) return;

		Vec3 slowdown = bush.getSlowdown();
		if (slowdown.x < 1.0 || slowdown.y < 1.0 || slowdown.z < 1.0) {
			entity.makeStuckInBlock(state, slowdown);
		}

		if (bush.damage <= 0.0F || level.isClientSide()) return;

		// Standing still in a bush is safe; it is pushing through one that scratches.
		double movedX = Math.abs(entity.getX() - entity.xOld);
		double movedZ = Math.abs(entity.getZ() - entity.zOld);
		if (movedX >= 0.003 || movedZ >= 0.003) {
			entity.hurt(damageSource(level), bush.damage);
		}
	}

	/**
	 * The damage a bush does, built from the declared damage type. An id that names nothing falls back to
	 * vanilla's own berry scratch rather than failing the hit, since a typo in a pack should not stop the
	 * block working.
	 */
	private DamageSource damageSource(Level level) {
		if (bush.damageType == null) return level.damageSources().sweetBerryBush();

		ResourceKey<DamageType> key = ResourceKey.create(Registries.DAMAGE_TYPE, bush.damageType);
		Optional<Holder.Reference<DamageType>> holder = level.registryAccess()
				.lookupOrThrow(Registries.DAMAGE_TYPE).get(key);
		return holder.isPresent() ? new DamageSource(holder.get()) : level.damageSources().sweetBerryBush();
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!state.is(oldState.getBlock())) {
			EventActionHandler.handleBlockEvent(level, pos, null, block, "on_place");
		}
		super.onPlace(state, level, pos, oldState, movedByPiston);
	}

	/* --------------------------------------- waterlogging -------------------------------------- */

	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockState placed = super.getStateForPlacement(ctx);
		if (placed == null || !waterloggable) return placed;

		boolean inWater = ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER;
		return placed.setValue(WATERLOGGED, inWater);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return waterloggable && state.getValue(WATERLOGGED)
				? Fluids.WATER.getSource(false)
				: super.getFluidState(state);
	}

	/* ---------------------------------------- bone meal ---------------------------------------- */

	@Override
	public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, BonemealSource source) {
		return state.getValue(age) < maxAge;
	}

	@Override
	public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
		return bush.bonemealSucceeds(random);
	}

	@Override
	public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
		int grown = Math.min(maxAge, state.getValue(age) + bush.bonemealIncrease(random));
		level.setBlock(pos, state.setValue(age, grown), 2);
	}
}
