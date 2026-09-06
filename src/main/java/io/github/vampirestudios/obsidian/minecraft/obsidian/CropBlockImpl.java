package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.EventActionHandler;
import io.github.vampirestudios.obsidian.api.obsidian.block.Growable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A crop.
 *
 * <p>Vanilla's crop fixes almost everything about growing: how bright it has to be, how fast it
 * advances, and how far one bone meal takes it. All of that is read from {@code growable} here instead,
 * and a crop that declares none of it behaves exactly as vanilla's does.
 *
 * <p>The age property stays vanilla's {@code age_7} whatever {@code max_age} says. Vanilla's own crop
 * code reads that property directly in places, so replacing it with a shorter one of our own would
 * break those; {@code max_age} caps how far the crop advances instead, and the generated blockstate
 * covers the ages past it.
 *
 * @see Growable
 */
public class CropBlockImpl extends CropBlock {

	public final io.github.vampirestudios.obsidian.api.obsidian.block.Block block;
	private final Growable growable;

	public CropBlockImpl(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Properties settings) {
		super(settings);
		this.block = block;
		this.growable = block != null && block.growable != null ? block.growable : new Growable();
	}

	@Override
	public int getMaxAge() {
		if (block.growable == null || block.growable.max_age <= 0) return super.getMaxAge();
		return Mth.clamp(block.growable.max_age, 1, super.getMaxAge());
	}

	@Override
	protected ItemLike getBaseSeedId() {
		if (growable.seed != null) {
			net.minecraft.world.item.Item seed = BuiltInRegistries.ITEM.getValue(growable.seed);
			if (seed != null) return seed;
		}
		return this.asItem();
	}

	/* ---------------------------------------- growing ------------------------------------------ */

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		EventActionHandler.handleBlockEvent(level, pos, null, block, "on_random_tick");

		// Nothing declared means vanilla's own growing, farmland formula and all — a crop in well-watered,
		// tended soil grows faster than one on dry ground, which is worth keeping when nobody asked to
		// change it.
		if (growable.growthChance <= 0 && growable.minLight == 9) {
			super.randomTick(state, level, pos, random);
			return;
		}

		int current = state.getValue(this.getAgeProperty());
		if (current >= this.getMaxAge()) return;
		if (level.getRawBrightness(pos, 0) < growable.minLight) return;
		// A declared rate replaces the farmland formula outright; 25 is roughly what that formula gives on
		// plain unwatered ground, so a crop that only moved its light level keeps growing at much the
		// same speed.
		if (random.nextInt(growable.growthChance(25)) != 0) return;

		level.setBlock(pos, state.setValue(this.getAgeProperty(), current + 1), 2);
	}

	@Override
	public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
		return growable.bonemealSucceeds(random);
	}

	@Override
	public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, BonemealSource source) {
		int grown = Math.min(this.getMaxAge(), state.getValue(this.getAgeProperty()) + growable.bonemealIncrease(random));
		level.setBlock(pos, state.setValue(this.getAgeProperty(), grown), 2);
	}

	/* --------------------------------------- harvesting ---------------------------------------- */

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		EventActionHandler.handleBlockEvent(level, player, block, "on_interact", hit);

		if (!growable.harvestOnInteract || state.getValue(this.getAgeProperty()) < this.getMaxAge()) {
			return super.useWithoutItem(state, level, pos, player, hit);
		}

		if (!level.isClientSide()) {
			// The crop's own loot, so a harvest gives exactly what breaking a grown one would — including
			// the age-conditioned drops that separate produce from seed, and the player's own tool bonuses.
			dropResources(state, level, pos, null, player, player.getMainHandItem());
			level.setBlock(pos, state.setValue(this.getAgeProperty(), 0), 2);
		}
		return InteractionResult.SUCCESS;
	}

	/* --------------------------------------- appearance ---------------------------------------- */

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		VoxelShape declared = BlockShapeUtils.shapeForAge(growable.shapesByAge, state.getValue(this.getAgeProperty()));
		return declared != null ? declared : super.getShape(state, level, pos, context);
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
		return block.information.getBlockSettings() != null ? !block.information.getBlockSettings().translucent ? 0.2F : 1.0F : super.getShadeBrightness(state, world, pos);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state) {
		return block.information.getBlockSettings() != null ? block.information.getBlockSettings().translucent : super.propagatesSkylightDown(state);
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter world, BlockPos pos) {
		if (block.can_plant_on == null || block.can_plant_on.isEmpty()) return super.mayPlaceOn(state, world, pos);
		return block.getSupportableBlocks().contains(state.getBlock());
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!state.is(oldState.getBlock())) {
			EventActionHandler.handleBlockEvent(level, pos, null, block, "on_place");
		}
		super.onPlace(state, level, pos, oldState, movedByPiston);
	}
}
