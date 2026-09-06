package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.BlockInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.MultiBlockVariants;
import io.github.vampirestudios.obsidian.api.obsidian.block.PlacementRules;
import io.github.vampirestudios.obsidian.api.obsidian.block.PlacementVariants;
import io.github.vampirestudios.obsidian.registry.properties.ListProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * The state properties a block definition can add to any block implementation — {@code placement} and
 * {@code part} — and the behavior that goes with them.
 *
 * <p>The implementations that support these each extend a different vanilla block, so this is a helper they
 * hold rather than a base class they share. Like {@code BlockImpl}'s own properties, the two are resolved
 * before {@code super(...)} runs and handed to {@code createBlockStateDefinition} out-of-band, since that is
 * called from the {@link Block} constructor before any subclass field can be assigned.
 */
public final class BlockVariants {

	private static final ThreadLocal<@Nullable BlockVariants> CONSTRUCTING = new ThreadLocal<>();

	public final io.github.vampirestudios.obsidian.api.obsidian.block.Block block;

	private final @Nullable ListProperty placement;
	private final @Nullable ListProperty part;
	/** The block's only variant, when it declared one and so carries no property for it. */
	private final @Nullable String solePlacement;
	private final @Nullable String solePart;
	/** Properties adopted from the implementation's own set are added by it, not by us. */
	private final boolean ownsPlacement;
	private final boolean ownsPart;

	private final io.github.vampirestudios.obsidian.api.obsidian.block.Block.@Nullable MultiBlockInformation multiBlock;

	/** Which surfaces the block may be placed against, or null when it declares no restriction. */
	private final @Nullable PlacementRules rules;

	/** A repeater's output strength. Only repeaters carry it; a plain power source emits a constant. */
	private final @Nullable IntegerProperty power;
	private final boolean ownsPower;

	/**
	 * Whether a {@code behaviour.lock} has already been opened. Only a lock that discards itself carries
	 * it — one that stays shut asks for the key every time and so has nothing to remember.
	 */
	private final @Nullable BooleanProperty unlocked;
	private final boolean ownsUnlocked;

	private BlockVariants(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Map<String, Property<?>> existing) {
		this.block = block;
		this.rules = PlacementRules.of(block);

		if (!RedstoneLogic.isRepeater(block)) {
			this.power = null;
			this.ownsPower = false;
		} else {
			// A pack may also have listed "power" in vanilla_properties; adding it twice fails the build.
			this.power = BlockStateProperties.POWER;
			this.ownsPower = !existing.containsValue(BlockStateProperties.POWER);
		}

		if (!LockLogic.discards(block)) {
			this.unlocked = null;
			this.ownsUnlocked = false;
		} else {
			// A pack may also have listed "unlocked" in vanilla_properties; adding it twice fails the build.
			this.unlocked = LockLogic.UNLOCKED;
			this.ownsUnlocked = !existing.containsValue(LockLogic.UNLOCKED);
		}

		Property<?> declaredPlacement = existing.get(PlacementVariants.PROPERTY);
		if (declaredPlacement instanceof ListProperty listProperty) {
			this.placement = listProperty;
			this.ownsPlacement = false;
			this.solePlacement = null;
		} else {
			List<String> placements = PlacementVariants.declared(block);
			boolean property = PlacementVariants.needsProperty(placements);
			this.placement = property ? ListProperty.create(PlacementVariants.PROPERTY, placements) : null;
			this.ownsPlacement = property;
			// One declared variant is still the block's variant; it just does not need a state to say so.
			this.solePlacement = !property && !placements.isEmpty() ? placements.getFirst() : null;
		}

		this.multiBlock = MultiBlockVariants.information(block);
		Property<?> declaredPart = existing.get(MultiBlockVariants.PROPERTY);
		if (declaredPart instanceof ListProperty listProperty) {
			this.part = listProperty;
			this.ownsPart = false;
			this.solePart = null;
		} else {
			List<String> cells = MultiBlockVariants.declared(block);
			boolean property = MultiBlockVariants.needsProperty(cells);
			this.part = property ? ListProperty.create(MultiBlockVariants.PROPERTY, cells) : null;
			this.ownsPart = property;
			// A one-cell structure is a plain block, so it never places or dismantles parts — but a shape
			// declared for that cell is still the shape it was given.
			this.solePart = !property && !cells.isEmpty() ? cells.getFirst() : null;
		}
	}

	/** Resolves a block's variants and stashes them; call from within the {@code super(...)} argument list. */
	public static Block.Properties prepare(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Block.Properties settings) {
		return prepare(block, settings, Map.of());
	}

	/**
	 * As {@link #prepare(io.github.vampirestudios.obsidian.api.obsidian.block.Block, Block.Properties)}, for
	 * implementations that resolve properties of their own: one already named {@code placement} or
	 * {@code part} is adopted rather than registered twice.
	 */
	public static Block.Properties prepare(io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
	                                       Block.Properties settings, Map<String, Property<?>> existing) {
		CONSTRUCTING.set(new BlockVariants(block, existing));
		return settings;
	}

	/** Adds the variant properties to the block being constructed. Only meaningful from {@code createBlockStateDefinition}. */
	public static void addTo(StateDefinition.Builder<Block, BlockState> builder) {
		BlockVariants variants = CONSTRUCTING.get();
		if (variants == null) return;

		if (variants.ownsPlacement) builder.add(variants.placement);
		if (variants.ownsPart) builder.add(variants.part);
		if (variants.ownsPower) builder.add(variants.power);
		if (variants.ownsUnlocked) builder.add(variants.unlocked);
	}

	/** A repeater's output property, or null when the block is not one. */
	public @Nullable IntegerProperty power() {
		return power;
	}

	/** The lock's "already opened" property, or null when the lock does not discard itself. */
	public @Nullable BooleanProperty unlocked() {
		return unlocked;
	}

	/** Hands the resolved variants to the block under construction, clearing the hand-off. */
	public static BlockVariants consume() {
		BlockVariants variants = CONSTRUCTING.get();
		CONSTRUCTING.remove();
		return variants;
	}

	public boolean isMultiBlock() {
		return part != null && multiBlock != null;
	}

	/**
	 * Fills in the variant properties for a placement, or returns null to fail it: when the surface clicked
	 * is one {@code behaviour.placement} does not allow, or when a multi-block has no room for the rest of
	 * its cells. A failed placement leaves nothing behind and the item in hand.
	 */
	public @Nullable BlockState onPlacement(BlockState state, BlockPlaceContext ctx, @Nullable Direction facing) {
		if (rules != null && !rules.allows(PlacementRules.surfaceFor(ctx.getClickedFace()))) return null;
		if (isMultiBlock() && !MultiBlocks.hasRoom(ctx, multiBlock, facing)) return null;

		if (placement != null) {
			String value = PlacementVariants.forClickedFace(ctx.getClickedFace(), placement.getPossibleValues());
			if (value != null) state = state.setValue(placement, value);
		}
		if (part != null && part.getPossibleValues().contains(MultiBlockVariants.ORIGIN)) {
			state = state.setValue(part, MultiBlockVariants.ORIGIN);
		}
		return state;
	}

	/** Builds the rest of a multi-block around the cell that was just placed. */
	public void afterPlace(Level level, BlockPos pos, BlockState state, @Nullable Direction facing) {
		if (!isMultiBlock() || level.isClientSide()) return;
		if (!MultiBlockVariants.ORIGIN.equals(partOf(state))) return;

		MultiBlocks.placeParts(level, pos, state, part, multiBlock, facing);
	}

	/** Takes the rest of a multi-block down when one of its cells is removed. */
	public void afterRemove(Level level, BlockPos pos, BlockState state, @Nullable Direction facing) {
		if (!isMultiBlock() || level.isClientSide()) return;

		MultiBlocks.dismantle(level, pos, state, part, multiBlock, facing);
	}

	/**
	 * The shape declared for this state's variant, most specific first: the multi-block cell, then the
	 * placement. Returns null when neither declares one, leaving the caller on the block-wide shape.
	 */
	public @Nullable VoxelShape shape(BlockState state, boolean outline, @Nullable Direction facing) {
		BlockInformation information = block.information;
		if (information == null) return null;

		String cell = partOf(state);
		if (cell != null && information.partShapes != null) {
			VoxelShape resolved = BlockShapeUtils.resolveVariant(information, information.partShapes.get(cell), outline, facing);
			if (resolved != null) return resolved;
		}

		String surface = placementOf(state);
		if (surface != null && information.placementShapes != null) {
			return BlockShapeUtils.resolveVariant(information, information.placementShapes.get(surface), outline, facing);
		}
		return null;
	}

	/**
	 * Whether a placed block still has the surface it was placed against.
	 *
	 * <p>Only blocks that restrict their placement are held to this — an unrestricted block is free-standing
	 * and never falls. The surface comes from the {@code placement} state property when the block records
	 * one, and otherwise from the restriction itself, which pins it down whenever only one surface is
	 * allowed. A block that allows several surfaces without recording which it used cannot be checked, so it
	 * is left standing rather than broken on a guess.
	 */
	public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos, @Nullable Direction facing) {
		if (rules == null) return true;

		String surface = placementOf(state);
		if (surface == null) surface = rules.onlySurface();
		if (surface == null) return true;

		return PlacementRules.supported(surface, level, pos, facing);
	}

	public @Nullable String placementOf(BlockState state) {
		if (placement != null && state.hasProperty(placement)) return state.getValue(placement);
		// A block with one declared variant records nothing, because there is nothing to choose.
		return solePlacement;
	}

	public @Nullable String partOf(BlockState state) {
		if (part != null && state.hasProperty(part)) return state.getValue(part);
		return solePart;
	}
}
