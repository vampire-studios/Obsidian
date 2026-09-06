package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.EventActionHandler;
import io.github.vampirestudios.obsidian.api.ISeatProvider;
import io.github.vampirestudios.obsidian.api.obsidian.block.PlacementVariants;
import io.github.vampirestudios.obsidian.registry.properties.ListProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class BlockImpl extends Block implements ISeatProvider {

	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Properties for the block currently being constructed. {@link #createBlockStateDefinition} runs from
	 * the {@link Block} constructor, before this subclass can assign any field, so the resolved property
	 * set has to be handed over out-of-band. The hand-off happens while the {@code super(...)} arguments
	 * are evaluated, which Java guarantees runs first.
	 */
	private static final ThreadLocal<Map<String, Property<?>>> CONSTRUCTING_PROPERTIES =
			ThreadLocal.withInitial(Map::of);

	/** Every property declared on {@code BlockStateProperties}, keyed by field name and, when unambiguous, by property name. */
	private static final Map<String, Property<?>> VANILLA_PROPERTIES = collectVanillaProperties();

	public final io.github.vampirestudios.obsidian.api.obsidian.block.Block block;
	private final Map<String, Property<?>> blockProperties;
	private final BlockVariants variants;

	public BlockImpl(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Properties settings) {
		super(prepare(block, settings));
		this.block = block;
		this.blockProperties = CONSTRUCTING_PROPERTIES.get();
		CONSTRUCTING_PROPERTIES.remove();
		this.variants = BlockVariants.consume();

		registerDefaultValues();
	}

	@Override
	public List<io.github.vampirestudios.obsidian.api.obsidian.block.Block.Behaviour.Seat> getSeats(BlockState state) {
		List<io.github.vampirestudios.obsidian.api.obsidian.block.Block.Behaviour.Seat> seats = SeatLogic.seatsOf(this.block);
		return seats != null ? seats : List.of();
	}

	/** Resolves this block's properties and stashes them for {@link #createBlockStateDefinition}. */
	private static Properties prepare(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Properties settings) {
		Map<String, Property<?>> properties = resolveProperties(block);
		CONSTRUCTING_PROPERTIES.set(properties);
		return BlockVariants.prepare(block, settings, properties);
	}

	private static Map<String, Property<?>> resolveProperties(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
		if (block.information == null) return Map.of();

		Map<String, Property<?>> resolved = new LinkedHashMap<>();

		if (block.information.properties != null) {
			block.information.properties.forEach((name, values) -> {
				if (values == null || values.length == 0) {
					LOGGER.warn("Custom block property \"{}\" declares no values; skipping it.", name);
					return;
				}
				resolved.put(name, ListProperty.create(name, List.of(values)));
			});
		}

		if (block.information.vanillaProperties != null) {
			for (String name : block.information.vanillaProperties) {
				Property<?> property = VANILLA_PROPERTIES.get(name.toLowerCase(Locale.ROOT));
				if (property == null) {
					LOGGER.warn("Unknown vanilla block property \"{}\".", name);
					continue;
				}
				if (resolved.putIfAbsent(name, property) != null) {
					LOGGER.warn("Vanilla block property \"{}\" is shadowed by a custom property of the same name.", name);
				}
			}
		}

		return Map.copyOf(resolved);
	}

	/**
	 * Indexes {@code BlockStateProperties} by reflection. Field names are always registered; the
	 * serialized property name is only registered when exactly one field carries it, since names such as
	 * {@code age} are shared by variants with different maximums.
	 */
	private static Map<String, Property<?>> collectVanillaProperties() {
		Map<String, Property<?>> byFieldName = new HashMap<>();
		Map<String, List<Property<?>>> byPropertyName = new HashMap<>();

		for (Field field : BlockStateProperties.class.getFields()) {
			if (!Property.class.isAssignableFrom(field.getType())) continue;
			try {
				Property<?> property = (Property<?>) field.get(null);
				if (property == null) continue;
				byFieldName.put(field.getName().toLowerCase(Locale.ROOT), property);
				byPropertyName.computeIfAbsent(property.getName(), _ -> new ArrayList<>()).add(property);
			} catch (IllegalAccessException e) {
				LOGGER.warn("Could not read vanilla block property {}.", field.getName(), e);
			}
		}

		Map<String, Property<?>> combined = new HashMap<>(byFieldName);
		byPropertyName.forEach((name, matches) -> {
			if (matches.size() == 1) combined.putIfAbsent(name, matches.getFirst());
		});
		return Map.copyOf(combined);
	}

	private void registerDefaultValues() {
		BlockState state = this.stateDefinition.any();

		Map<String, String> defaults = block.information != null ? block.information.defaultValue : null;
		if (defaults != null) {
			for (Map.Entry<String, String> entry : defaults.entrySet()) {
				Property<?> property = blockProperties.get(entry.getKey());
				if (property == null) {
					LOGGER.warn("Default value given for unknown block property \"{}\".", entry.getKey());
					continue;
				}
				state = withValue(state, property, entry.getValue());
			}
		}

		this.registerDefaultState(state);
	}

	private static <T extends Comparable<T>> BlockState withValue(BlockState state, Property<T> property, String value) {
		Optional<T> parsed = property.getValue(value);
		if (parsed.isEmpty()) {
			LOGGER.warn("\"{}\" is not a valid value for block property \"{}\".", value, property.getName());
			return state;
		}
		return state.setValue(property, parsed.get());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		CONSTRUCTING_PROPERTIES.get().values().forEach(builder::add);
		BlockVariants.addTo(builder);
	}

	/**
	 * Whether the block being constructed already declares this property, so subclasses adding their own
	 * properties can avoid registering a duplicate — which would otherwise fail the state definition.
	 * Only meaningful from within {@link #createBlockStateDefinition}.
	 */
	protected static boolean declaresProperty(Property<?> property) {
		return CONSTRUCTING_PROPERTIES.get().containsValue(property);
	}

	protected static boolean declaresPropertyNamed(String name) {
		for (Property<?> property : CONSTRUCTING_PROPERTIES.get().values()) {
			if (property.getName().equals(name)) return true;
		}
		return false;
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockState state = super.getStateForPlacement(ctx);
		if (state == null) return null;

		for (Map.Entry<String, Property<?>> entry : blockProperties.entrySet()) {
			state = applyPlacementValue(state, entry.getKey(), entry.getValue(), ctx);
		}
		return variants.onPlacement(state, ctx, facingOf(state));
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		variants.afterPlace(level, pos, state, facingOf(state));
	}

	/** Fills in the placement-dependent properties Obsidian knows how to derive; leaves the rest at their default. */
	private static BlockState applyPlacementValue(BlockState state, String name, Property<?> property, BlockPlaceContext ctx) {
		if (property == BlockStateProperties.FACING) {
			return state.setValue(BlockStateProperties.FACING, ctx.getClickedFace());
		}
		if (property == BlockStateProperties.HORIZONTAL_FACING) {
			return state.setValue(BlockStateProperties.HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite());
		}
		if (property == BlockStateProperties.AXIS) {
			return state.setValue(BlockStateProperties.AXIS, ctx.getClickedFace().getAxis());
		}
		if (property instanceof ListProperty listProperty && name.equals(PlacementVariants.PROPERTY)) {
			String value = PlacementVariants.forClickedFace(ctx.getClickedFace(), listProperty.getPossibleValues());
			if (value != null) return state.setValue(listProperty, value);
		}
		return state;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		// The lock comes first: a locked block runs no events and offers no seat until it is opened.
		InteractionResult locked = LockLogic.tryUnlock(level, pos, state, player, InteractionHand.MAIN_HAND, block, variants);
		if (locked != InteractionResult.PASS) return locked;

		EventActionHandler.handleBlockEvent(level, player, block, "on_interact", hit);

		InteractionResult seated = SeatLogic.sit(level, pos, state, player, hit);
		if (seated != InteractionResult.PASS) return seated;

		return super.useWithoutItem(state, level, pos, player, hit);
	}

	@Override
	protected void attack(BlockState state, Level level, BlockPos pos, Player player) {
		EventActionHandler.handleBlockEvent(level, pos, player, block, "on_attack");
		super.attack(state, level, pos, player);
	}

	@Override
	public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
		EventActionHandler.handleBlockEvent(level, pos, entity instanceof Player player ? player : null, block, "on_step_on");
		super.stepOn(level, pos, state, entity);
	}

	@Override
	protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean bl) {
		EventActionHandler.handleBlockEvent(level, pos, entity instanceof Player player ? player : null, block, "on_entity_inside");
		// A block named by a world/portal carries travellers; anything else costs one map lookup.
		PortalLogic.tryEnter(state, pos, entity);
		super.entityInside(state, level, pos, entity, applier, bl);
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!state.is(oldState.getBlock())) {
			EventActionHandler.handleBlockEvent(level, pos, null, block, "on_place");
		}
		// A repeater dropped next to a live wire has to catch up to it, having missed the change.
		RedstoneLogic.onNeighbourChanged(block, level, pos, state, variants.power(), facingOf(state));
		super.onPlace(state, level, pos, oldState, movedByPiston);
	}

	@Override
	protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
		EventActionHandler.handleBlockEvent(level, pos, null, block, "on_remove");
		SeatLogic.clearSeats(level, pos);
		variants.afterRemove(level, pos, state, facingOf(state));
		super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		EventActionHandler.handleBlockEvent(level, pos, null, block, "on_random_tick");
		super.randomTick(state, level, pos, random);
	}

	@Override
	protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack, boolean dropExperience) {
		super.spawnAfterBreak(state, level, pos, stack, dropExperience);
		if (dropExperience) {
			if (block.dropInformation != null) this.popExperience(level, pos, block.dropInformation.xpDropAmount);
		}
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
		float defaultValue = super.getShadeBrightness(state, world, pos);
		if (block.information.getBlockSettings() != null) {
			if (block.information.getBlockSettings().getParentSettings() != null) {
				return !block.information.getBlockSettings().getParentSettings().translucent ? 0.2F : 1.0F;
			} else {
				return !block.information.getBlockSettings().translucent ? 0.2F : 1.0F;
			}
		} else {
			return defaultValue;
		}
	}

	@Override
	public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter world, BlockPos pos) {
		boolean defaultValue = super.isCollisionShapeFullBlock(state, world, pos);
		if (block.information.getBlockSettings() != null) {
			if (block.information.getBlockSettings().getParentSettings() != null) {
				return !block.information.getBlockSettings().getParentSettings().translucent;
			} else {
				return !block.information.getBlockSettings().translucent;
			}
		} else {
			return defaultValue;
		}
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state) {
		boolean defaultValue = super.propagatesSkylightDown(state);
		if (block.information.getBlockSettings() != null) {
			if (block.information.getBlockSettings().getParentSettings() != null) {
				return block.information.getBlockSettings().getParentSettings().translucent;
			} else {
				return block.information.getBlockSettings().translucent;
			}
		} else {
			return defaultValue;
		}
	}

	@Override
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
		return resolved != null ? resolved : BlockShapeUtils.occlusionFallback(block);
	}

	/** The variant's shape when this state has one, otherwise the block-wide shape. */
	private @Nullable VoxelShape shapeOf(BlockState state, boolean outline) {
		Direction facing = facingOf(state);

		VoxelShape variantShape = variants.shape(state, outline, facing);
		if (variantShape != null) return variantShape;

		return BlockShapeUtils.resolve(outline ? block.information.outlineShape : block.information.collisionShape,
				block.information.shape, block.information.shapes, facing);
	}

	@Override
	protected boolean isSignalSource(BlockState state) {
		return RedstoneLogic.isSignalSource(block);
	}

	@Override
	@NullMarked
	protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction towards) {
		return RedstoneLogic.signal(block, state, towards, variants.power(), facingOf(state));
	}

	@Override
	@NullMarked
	protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction towards) {
		// A repeater powers through the block it points at, the way a vanilla one does; a plain power
		// source only powers what touches it.
		return RedstoneLogic.isRepeater(block) ? getSignal(state, level, pos, towards) : 0;
	}

	@Override
	@NullMarked
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock,
	                            Orientation orientation, boolean movedByPiston) {
		super.neighborChanged(state, level, pos, sourceBlock, orientation, movedByPiston);
		RedstoneLogic.onNeighbourChanged(block, level, pos, state, variants.power(), facingOf(state));
	}

	@Override
	@NullMarked
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		super.tick(state, level, pos, random);
		RedstoneLogic.onScheduledTick(block, level, pos, state, variants.power(), facingOf(state));
	}

	@Override
	@NullMarked
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		return variants.canSurvive(state, level, pos, facingOf(state)) && super.canSurvive(state, level, pos);
	}

	@Override
	@NullMarked
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos,
	                                 Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
		if (!state.canSurvive(level, pos)) return net.minecraft.world.level.block.Blocks.AIR.defaultBlockState();
		return super.updateShape(state, level, tickAccess, pos, direction, neighborPos, neighborState, random);
	}

	private @Nullable Direction facingOf(BlockState state) {
		return SeatLogic.facingOf(state);
	}

}
