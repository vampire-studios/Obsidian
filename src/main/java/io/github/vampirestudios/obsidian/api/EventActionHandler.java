package io.github.vampirestudios.obsidian.api;

import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.obsidian.item.Item;
import io.github.vampirestudios.obsidian.api.obsidian.item.UseActions;
import io.github.vampirestudios.obsidian.api.obsidian.menu.CustomMenuConfig;
import io.github.vampirestudios.obsidian.minecraft.CrateMenus;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.OItemComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import io.github.vampirestudios.obsidian.api.obsidian.world.MobSpec;
import io.github.vampirestudios.obsidian.minecraft.obsidian.MobSpawner;
import io.github.vampirestudios.obsidian.minecraft.obsidian.WorldEventManager;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.Prediction;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;
import org.joml.Vector3fc;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class EventActionHandler {

	private static final Logger LOGGER = LogManager.getLogger();

	/** Drives the optional {@code chance} on any action. */
	private static final RandomSource RANDOM = RandomSource.create();

	/** Actions that act on a block position. They need a context with a {@code pos}, and no player. */
	private static final Set<String> POSITIONAL_ACTIONS = Set.of(
			"set_block", "set_block_at_pos", "fill_blocks", "set_block_property", "spawn_loot", "play_sound_at",
			"spawn_entity", "drop_item", "spawn_particles_at", "strike_lightning",
			"start_event", "advance_event", "stop_event");

	/** Actions that need both a player and the block they clicked. */
	private static final Set<String> USE_ON_ACTIONS = Set.of("convert_item");

	/**
	 * Whether an action acts on a player rather than on the world.
	 *
	 * <p>A trigger with several players to choose from — a world event, say — has to run these once per
	 * player and the rest once in total, or a single lightning strike becomes one per participant.
	 */
	public static boolean needsPlayer(@Nullable String action) {
		return action != null && !POSITIONAL_ACTIONS.contains(action);
	}

	/** The {@code set_block_property} value that steps a property along instead of setting it outright. */
	private static final String TOGGLE = "toggle";
	private static final int DEFAULT_AMOUNT_REQUIRED = 1;
	private static final int DEFAULT_PARTICLE_COUNT = 3;
	private static final float DEFAULT_SOUND_VOLUME = 0.5F;
	private static final float DEFAULT_SOUND_PITCH = 1.0F;
	private static final int DEFAULT_COOLDOWN = 0;
	private static final int DEFAULT_XP_AMOUNT = 0;
	private static final int DEFAULT_GUI_SIZE = 3;
	private static final int DEFAULT_ITEM_COUNT = 1;
	private static final int DEFAULT_DURABILITY_LOSS = 1;
	private static final int MAX_FILL_VOLUME = 512;

	public static void handleOnUse(Player player, Item item) {
		handleEventActions(InteractionContext.of(player), item, "on_use");
	}

	public static void handleHurtEnemy(LivingEntity target, Player attacker, Item item) {
		handleEventActions(InteractionContext.ofEntity(attacker, target), item, "hurt_enemy");
	}

	public static void handleOnMiningBlock(Player miningEntity, BlockState state, BlockPos pos, Item item, net.minecraft.world.item.Item minecraftItem) {
		List<Map<String, Object>> actions = item.getEventActions("mine_block");

		for (Map<String, Object> actionConfig : actions) {
			String action = (String) actionConfig.get("action");

			boolean shouldExecute = false;
			if (actionConfig.containsKey("blocks")) {
				List<Identifier> blocks = ((List<String>) actionConfig.get("blocks")).stream()
						.map(Identifier::tryParse).toList();
				shouldExecute = blocks.contains(BuiltInRegistries.BLOCK.getKey(state.getBlock()));
			}

			if (actionConfig.containsKey("tags")) {
				List<String> tags = (List<String>) actionConfig.get("tags");
				for (String tag : tags) {
					if (state.is(TagKey.create(Registries.BLOCK, Identifier.tryParse(tag)))) {
						shouldExecute = true;
						break;
					}
				}
			}

			if (pos != null) {
				if (actionConfig.containsKey("position")) {
					List<Double> position = (List<Double>) actionConfig.get("position");
					if (pos.equals(new BlockPos(position.get(0).intValue(), position.get(1).intValue(), position.get(2).intValue()))) {
						shouldExecute = true;
					}
				}

				if (actionConfig.containsKey("min_position") && actionConfig.containsKey("max_position")) {
					List<Double> minPosition = (List<Double>) actionConfig.get("min_position");
					List<Double> maxPosition = (List<Double>) actionConfig.get("max_position");

					int minX = minPosition.get(0).intValue();
					int minY = minPosition.get(1).intValue();
					int minZ = minPosition.get(2).intValue();
					int maxX = maxPosition.get(0).intValue();
					int maxY = maxPosition.get(1).intValue();
					int maxZ = maxPosition.get(2).intValue();

					if (pos.getX() >= minX && pos.getX() <= maxX &&
							pos.getY() >= minY && pos.getY() <= maxY &&
							pos.getZ() >= minZ && pos.getZ() <= maxZ) {
						shouldExecute = true;
					}
				}
			}

			if (!actionConfig.containsKey("blocks") && !actionConfig.containsKey("tags")
					&& !actionConfig.containsKey("position")
					&& !(actionConfig.containsKey("min_position") && actionConfig.containsKey("max_position"))
					&& !actionConfig.containsKey("radius")
					&& !(actionConfig.containsKey("width") && actionConfig.containsKey("height") && actionConfig.containsKey("depth"))
			) {
				shouldExecute = true; // For generic actions
			}

			if (shouldExecute) {
				ItemStack stack = minecraftItem.getDefaultInstance();
				if (minecraftItem.getDefaultInstance().has(OItemComponents.MINING_RADIUS)) {
					int radius = stack.get(OItemComponents.MINING_RADIUS);
					executeMiningInRadius(miningEntity, pos, radius);
				}
				if (minecraftItem.getDefaultInstance().has(OItemComponents.MINING_AREA)) {
					Vector3fc vector3f = stack.get(OItemComponents.MINING_AREA);
					assert vector3f != null;
					executeMiningInArea(miningEntity, pos, (int) vector3f.x(), (int) vector3f.y(), (int) vector3f.z());
				}

				// Mining knows where it happened, so positional actions such as set_block work here too.
				dispatch(InteractionContext.ofBlock(miningEntity.level(), pos, miningEntity, null), action, actionConfig);
			}
		}
	}

	private static void executeMiningInRadius(Player player, BlockPos centerPos, int radius) {
		for (int x = -radius; x <= radius; x++) {
			for (int y = -radius; y <= radius; y++) {
				BlockPos pos = centerPos.offset(x, y, 0);
				player.level().destroyBlock(pos, true);
			}
		}
	}

	private static void executeMiningInArea(Player player, BlockPos centerPos, int width, int height, int depth) {
		int halfWidth = width / 2;
		int halfHeight = height / 2;
		int halfDepth = depth / 2;

		for (int x = -halfWidth; x <= halfWidth; x++) {
			for (int y = -halfHeight; y <= halfHeight; y++) {
				for (int z = -halfDepth; z <= halfDepth; z++) {
					BlockPos pos = centerPos.offset(x, y, z);
					player.level().destroyBlock(pos, true);
				}
			}
		}
	}

	public static void handleOnItemCrafted(Player player, Item item) {
		handleEventActions(InteractionContext.of(player), item, "craft");
	}

	public static void handleOnUseTick(Player player, Item item) {
		handleEventActions(InteractionContext.of(player), item, "use_tick");
	}

	public static void handleOnUseOn(UseOnContext useOnContext, Item item) {
		handleEventActions(InteractionContext.of(useOnContext), item, "use_on");
	}

	public static void handleOnFinishUsing(Player player, Item item) {
		handleEventActions(InteractionContext.of(player), item, "finish_using");
	}

	public static void handleOnInventoryTick(Player player, Item item) {
		handleEventActions(InteractionContext.of(player), item, "inventory_tick");
	}

	/**
	 * Fired when the player right-clicks a living entity with the item. This is the only item event
	 * besides {@code hurt_enemy} that supplies a target, so target-aware actions such as
	 * {@code ignite} apply to the clicked entity here.
	 */
	public static void handleInteractEntity(Player player, LivingEntity target, Item item) {
		handleEventActions(InteractionContext.ofEntity(player, target), item, "interact_entity");
	}

	/**
	 * Fired when the player releases a right-click before the use duration elapsed. The counterpart to
	 * {@code finish_using}, which only fires when the use completes.
	 */
	public static void handleOnStopUsing(Player player, Item item) {
		handleEventActions(InteractionContext.of(player), item, "stop_using");
	}

	/**
	 * Fired when a dropped stack of this item is destroyed in the world (fire, lava, explosion).
	 * Only fires when the item entity has a player owner, since actions act on a player.
	 */
	public static void handleOnDestroyed(Player player, Item item) {
		handleEventActions(InteractionContext.of(player), item, "destroyed");
	}

	public static void handleOnPickup(Player player, Item item) {
		handleEventActions(InteractionContext.of(player), item, "on_pickup");
	}

	/**
	 * Fired the tick an item turns up in one of the player's equipment slots, however it got there —
	 * dragged in the inventory screen, right-clicked on, given by a command or dropped in by a dispenser.
	 */
	public static void handleOnEquip(Player player, EquipmentSlot slot, Item item) {
		handleEventActions(InteractionContext.ofEquipment(player, slot), item, "on_equip");
	}

	/**
	 * Fired the tick an item leaves an equipment slot. The stack is already gone by then, so the context
	 * points at whatever now occupies the slot — usually nothing. Undo whatever {@code on_equip} did here.
	 */
	public static void handleOnUnequip(Player player, EquipmentSlot slot, Item item) {
		handleEventActions(InteractionContext.ofEquipment(player, slot), item, "on_unequip");
	}

	/** Fired every tick the item stays in an equipment slot, for effects that need renewing. */
	public static void handleEquipmentTick(Player player, EquipmentSlot slot, Item item) {
		handleEventActions(InteractionContext.ofEquipment(player, slot), item, "equipment_tick");
	}

	/**
	 * Fired once per projectile a ranged weapon launches — three times for a multishot crossbow, not once.
	 */
	public static void handleOnShoot(Player shooter, Item item) {
		handleEventActions(InteractionContext.of(shooter), item, "on_shoot");
	}

	/**
	 * Fired when a projectile launched by this item strikes an entity. The target is the entity struck, so
	 * {@code ignite} and {@code damage} apply to it; a non-living entity leaves the target empty.
	 */
	public static void handleProjectileHitEntity(Projectile projectile, @Nullable Player shooter,
												 @Nullable LivingEntity struck, Item item) {
		handleEventActions(InteractionContext.ofProjectile(projectile.level(), shooter, struck, null),
				item, "projectile_hit_entity");
	}

	/** Fired when a projectile launched by this item strikes a block, which is where it comes to rest. */
	public static void handleProjectileHitBlock(Projectile projectile, @Nullable Player shooter,
												BlockHitResult hit, Item item) {
		handleEventActions(InteractionContext.ofProjectile(projectile.level(), shooter, null, hit),
				item, "projectile_hit_block");
	}

	/**
	 * Runs a block's action list for an event. A block event may have no player at all
	 * ({@code on_random_tick} never does, {@code on_entity_inside} usually does not); the context records
	 * that, and {@link #dispatch} skips player-scoped actions with a warning when there is nobody to apply
	 * them to.
	 */
	public static void handleBlockEvent(Level level, BlockPos pos, @Nullable Player player,
										io.github.vampirestudios.obsidian.api.obsidian.block.Block block, String event) {
		handleBlockEvent(level, pos, player, block, event, null);
	}

	public static void handleBlockEvent(Level level, BlockPos pos, @Nullable Player player,
										io.github.vampirestudios.obsidian.api.obsidian.block.Block block, String event,
										@Nullable Direction facing) {
		runBlockEvent(level, block, event, InteractionContext.ofBlock(level, pos, player, facing));
	}

	/**
	 * The same, for the interactions that know exactly where on the block the player clicked. The extra
	 * precision reaches actions and conditions through the context's hit position and facing.
	 */
	public static void handleBlockEvent(Level level, @Nullable Player player,
										io.github.vampirestudios.obsidian.api.obsidian.block.Block block, String event,
										BlockHitResult hit) {
		runBlockEvent(level, block, event, InteractionContext.ofHit(level, player, hit));
	}

	private static void runBlockEvent(Level level, io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
									  String event, InteractionContext ctx) {
		if (level.isClientSide()) return;

		for (Map<String, Object> actionConfig : block.getEventActions(event)) {
			dispatch(ctx, (String) actionConfig.get("action"), actionConfig, event);
		}
	}

	/** Handles the actions that act on a position rather than a player. */
	private static void handlePositionalAction(InteractionContext ctx, String action, Map<String, Object> actionConfig) {
		Level level = ctx.level();
		BlockPos pos = ctx.pos();
		switch (action) {
			case "set_block" -> setBlockAction(level, pos, actionConfig);
			case "set_block_at_pos" -> setBlockAtPosAction(level, pos, actionConfig, ctx.facing());
			case "fill_blocks" -> fillBlocksAction(level, pos, actionConfig, ctx.facing());
			case "set_block_property" -> setBlockPropertyAction(level, pos, actionConfig);
			case "spawn_loot" -> spawnLootAction(level, pos, actionConfig);
			case "play_sound_at" -> playSoundAtAction(level, pos, actionConfig);
			case "spawn_entity" -> spawnEntityAction(ctx, actionConfig);
			case "drop_item" -> dropItemAction(ctx, actionConfig);
			case "spawn_particles_at" -> spawnParticlesAtAction(ctx, actionConfig);
			case "strike_lightning" -> strikeLightningAction(ctx, actionConfig);
			case "start_event", "advance_event", "stop_event" -> worldEventAction(ctx, action, actionConfig);
			default -> LOGGER.warn("Unknown positional action: {}", action);
		}
	}

	/**
	 * Starts, advances or stops a {@code world/event} at this position.
	 *
	 * <p>Positional rather than player-scoped: an event belongs to a place, and the same action has to
	 * work from a block tick or a pattern with nobody attached to it.
	 */
	private static void worldEventAction(InteractionContext ctx, String action, Map<String, Object> actionConfig) {
		if (!(ctx.level() instanceof ServerLevel level)) return;

		Object declared = actionConfig.get("event");
		Identifier id = declared == null ? null : Identifier.tryParse(String.valueOf(declared));
		if (id == null) {
			LOGGER.warn("Action \"{}\" needs an \"event\" id.", action);
			return;
		}

		switch (action) {
			case "start_event" -> WorldEventManager.start(level, ctx.pos(), id);
			case "advance_event" -> WorldEventManager.advance(level, ctx.pos(), id);
			case "stop_event" -> WorldEventManager.stop(level, ctx.pos(), id);
			default -> LOGGER.warn("Unhandled world event action: {}", action);
		}
	}

	private static void setBlockAction(Level level, BlockPos pos, Map<String, Object> actionConfig) {
		Block target = resolveBlock(actionConfig);
		if (target == null) return;
		level.setBlockAndUpdate(pos, target.defaultBlockState());
	}

	private static void setBlockAtPosAction(Level level, BlockPos pos, Map<String, Object> actionConfig,
											@Nullable Direction facing) {
		Block target = resolveBlock(actionConfig);
		if (target == null) return;

		BlockPos at = pos.offset(rotate(
				((Number) actionConfig.getOrDefault("x", 0)).intValue(),
				((Number) actionConfig.getOrDefault("y", 0)).intValue(),
				((Number) actionConfig.getOrDefault("z", 0)).intValue(),
				facing));
		if (!matchesReplace(level, at, actionConfig)) return;
		level.setBlockAndUpdate(at, target.defaultBlockState());
	}

	/**
	 * Fills a box of blocks relative to the one that fired the event, so a prop can lay down its own
	 * collision on placement and clear it again on removal. Offsets rotate with the block's facing.
	 */
	private static void fillBlocksAction(Level level, BlockPos pos, Map<String, Object> actionConfig,
										 @Nullable Direction facing) {
		Block target = resolveBlock(actionConfig);
		if (target == null) return;

		int[] from = offsetArray(actionConfig, "from");
		int[] to = offsetArray(actionConfig, "to");
		if (from == null || to == null) {
			LOGGER.warn("fill_blocks action needs both a \"from\" and a \"to\" offset.");
			return;
		}

		long volume = (Math.abs(to[0] - from[0]) + 1L) * (Math.abs(to[1] - from[1]) + 1L) * (Math.abs(to[2] - from[2]) + 1L);
		if (volume > MAX_FILL_VOLUME) {
			LOGGER.warn("fill_blocks action covers {} blocks, over the {} limit; skipped.", volume, MAX_FILL_VOLUME);
			return;
		}

		BlockState state = target.defaultBlockState();
		for (int x = Math.min(from[0], to[0]); x <= Math.max(from[0], to[0]); x++) {
			for (int y = Math.min(from[1], to[1]); y <= Math.max(from[1], to[1]); y++) {
				for (int z = Math.min(from[2], to[2]); z <= Math.max(from[2], to[2]); z++) {
					BlockPos at = pos.offset(rotate(x, y, z, facing));
					if (at.equals(pos)) continue;   // never overwrite the block that fired the event
					if (!matchesReplace(level, at, actionConfig)) continue;
					level.setBlockAndUpdate(at, state);
				}
			}
		}
	}

	/**
	 * Rotates a local offset to the block's horizontal facing, matching how shapes are rotated, so a
	 * layout authored for a north-facing block follows it around.
	 */
	private static Vec3i rotate(int x, int y, int z, @Nullable Direction facing) {
		if (facing == null || !facing.getAxis().isHorizontal()) return new Vec3i(x, y, z);
		return switch (facing) {
			case SOUTH -> new Vec3i(-x, y, -z);
			case WEST -> new Vec3i(z, y, -x);
			case EAST -> new Vec3i(-z, y, x);
			default -> new Vec3i(x, y, z);
		};
	}

	/** An optional "replace" block id restricts what may be overwritten — usually air when placing. */
	private static boolean matchesReplace(Level level, BlockPos at, Map<String, Object> actionConfig) {
		String replace = (String) actionConfig.get("replace");
		if (replace == null) return true;
		Block expected = BuiltInRegistries.BLOCK.getValue(Identifier.tryParse(replace));
		if (expected == null) {
			LOGGER.warn("Unknown block \"{}\" in \"replace\".", replace);
			return false;
		}
		return level.getBlockState(at).is(expected);
	}

	private static int @Nullable [] offsetArray(Map<String, Object> actionConfig, String key) {
		if (!(actionConfig.get(key) instanceof List<?> list) || list.size() != 3) return null;
		int[] out = new int[3];
		for (int i = 0; i < 3; i++) {
			if (!(list.get(i) instanceof Number n)) return null;
			out[i] = n.intValue();
		}
		return out;
	}

	/** Sets a single state property on the block that fired the event, leaving its other properties alone. */
	private static void setBlockPropertyAction(Level level, BlockPos pos, Map<String, Object> actionConfig) {
		String name = (String) actionConfig.get("property");
		String value = String.valueOf(actionConfig.get("value"));
		if (name == null || actionConfig.get("value") == null) {
			LOGGER.warn("set_block_property action needs both a \"property\" and a \"value\".");
			return;
		}

		BlockState state = level.getBlockState(pos);
		Property<?> property = state.getBlock().getStateDefinition().getProperty(name);
		if (property == null) {
			LOGGER.warn("Block at {} has no property \"{}\".", pos, name);
			return;
		}

		// "toggle" steps to the property's next value and wraps, which on a boolean is a flip.
		BlockState updated = TOGGLE.equalsIgnoreCase(value) ? state.cycle(property) : setProperty(state, property, value);
		if (updated != state) level.setBlockAndUpdate(pos, updated);
	}

	private static <T extends Comparable<T>> BlockState setProperty(BlockState state, Property<T> property, String value) {
		Optional<T> parsed = property.getValue(value);
		if (parsed.isEmpty()) {
			LOGGER.warn("\"{}\" is not a valid value for block property \"{}\".", value, property.getName());
			return state;
		}
		return state.setValue(property, parsed.get());
	}

	/** Drops the block's own loot table at its position, without removing the block. */
	private static void spawnLootAction(Level level, BlockPos pos, Map<String, Object> actionConfig) {
		if (!(level instanceof ServerLevel serverLevel)) return;

		BlockState state = level.getBlockState(pos);
		for (ItemStack drop : Block.getDrops(state, serverLevel, pos, null)) {
			Block.popResource(level, pos, drop);
		}
	}

	private static void playSoundAtAction(Level level, BlockPos pos, Map<String, Object> actionConfig) {
		String soundId = (String) actionConfig.get("sound");
		if (soundId == null) {
			LOGGER.warn("play_sound_at action is missing the required \"sound\" value.");
			return;
		}
		SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.getValue(Identifier.tryParse(soundId));
		if (soundEvent == null) {
			LOGGER.warn("Unknown sound \"{}\" in play_sound_at action.", soundId);
			return;
		}
		float volume = ((Number) actionConfig.getOrDefault("volume", DEFAULT_SOUND_VOLUME)).floatValue();
		float pitch = ((Number) actionConfig.getOrDefault("pitch", DEFAULT_SOUND_PITCH)).floatValue();
		level.playSound(null, pos, soundEvent, SoundSource.BLOCKS, volume, pitch);
	}

	private static @Nullable Block resolveBlock(Map<String, Object> actionConfig) {
		String blockId = (String) actionConfig.get("block");
		if (blockId == null) {
			LOGGER.warn("Block action is missing the required \"block\" value.");
			return null;
		}
		Block target = BuiltInRegistries.BLOCK.getValue(Identifier.tryParse(blockId));
		if (target == null) LOGGER.warn("Unknown block \"{}\" in block action.", blockId);
		return target;
	}

	/** Runs every action an item declares for an event, in the context the trigger supplied. */
	private static void handleEventActions(InteractionContext ctx, Item item, String event) {
		for (Map<String, Object> actionConfig : item.getEventActions(event)) {
			dispatch(ctx, (String) actionConfig.get("action"), actionConfig, event);
		}
	}

	public static void dispatch(InteractionContext ctx, @Nullable String action, Map<String, Object> actionConfig) {
		dispatch(ctx, action, actionConfig, "unknown");
	}

	/**
	 * Runs one action against a context.
	 *
	 * <p>Actions are grouped by what they need rather than by what declared them, so any trigger reaches any
	 * action whose needs its context can meet: a block event with a player behind it reaches the player
	 * actions, and an item used on a block reaches the positional ones. An action whose needs are not met is
	 * skipped with a warning naming the event, which is the common case of a pack author moving an action to
	 * a trigger that cannot supply what it wants.
	 */
	public static void dispatch(InteractionContext ctx, @Nullable String action, Map<String, Object> actionConfig, String event) {
		if (action == null) {
			LOGGER.warn("Event action is missing the required \"action\" key: {}", actionConfig);
			return;
		}
		if (!rolled(actionConfig)) return;

		try {
			String missing = missingContext(ctx, action);
			if (missing != null) {
				LOGGER.warn("Action \"{}\" needs {}, but the \"{}\" event does not have one.", action, missing, event);
				return;
			}

			// Checked after the context, so an action that cannot run anyway never consumes a key.
			if (!EventConditions.pass(ctx, actionConfig)) return;

			if (POSITIONAL_ACTIONS.contains(action)) {
				handlePositionalAction(ctx, action, actionConfig);
			} else if (USE_ON_ACTIONS.contains(action)) {
				handleUseOnAction(ctx, action, actionConfig);
			} else {
				handlePlayerAction(ctx, action, actionConfig);
			}
		} catch (Exception e) {
			LOGGER.error("Failed to handle action: {}", action, e);
		}
	}

	/** What the action needs and the context lacks, phrased for the log, or null when it can run. */
	private static @Nullable String missingContext(InteractionContext ctx, String action) {
		if (POSITIONAL_ACTIONS.contains(action)) {
			return ctx.pos() == null ? "a block position" : null;
		}
		if (USE_ON_ACTIONS.contains(action)) {
			if (ctx.pos() == null) return "a block position";
			return ctx.player() == null ? "a player" : null;
		}
		return ctx.player() == null ? "a player" : null;
	}

	/**
	 * Whether an action's optional {@code chance} lets it run this time. A chance of {@code 0.25} runs the
	 * action a quarter of the time; an action without one always runs. Values outside 0-1 are clamped.
	 */
	private static boolean rolled(Map<String, Object> actionConfig) {
		Object chance = actionConfig.get("chance");
		if (chance == null) return true;

		if (!(chance instanceof Number number)) {
			LOGGER.warn("Action \"chance\" must be a number between 0 and 1, but was: {}", chance);
			return true;
		}
		return RANDOM.nextDouble() < number.doubleValue();
	}

	/** Handles the actions that act on a player, and on the target where the trigger supplies one. */
	private static void handlePlayerAction(InteractionContext ctx, String action, Map<String, Object> actionConfig) {
		Player player = ctx.player();
		LivingEntity target = ctx.target();
		switch (action) {
			case "open_gui":
				openGuiAction(player, actionConfig);
				break;
			case "open_crate":
				openCrateAction(ctx, actionConfig);
				break;
			case "send_message":
				sendMessageAction(player, actionConfig);
				break;
			case "apply_effect":
				applyEffectAction(player, actionConfig);
				break;
			case "give_experience":
				giveExperienceAction(player, actionConfig);
				break;
			case "give_experience_levels":
				giveExperienceLevelsAction(player, actionConfig);
				break;
			case "give_item":
				giveItemAction(player, actionConfig);
				break;
			case "consume_item":
				consumeItemAction(ctx, actionConfig);
				break;
			case "damage_item":
				damageItemAction(ctx, actionConfig);
				break;
			case "set_cooldown":
				setCooldownAction(ctx, actionConfig);
				break;
			case "heal":
				healAction(player, actionConfig);
				break;
			case "feed":
				feedAction(player, actionConfig);
				break;
			case "remove_effect":
				removeEffectAction(player, actionConfig);
				break;
			case "clear_effects":
				player.removeAllEffects();
				break;
			case "extinguish":
				extinguishAction(player, target);
				break;
			case "play_sound":
				playSoundAction(player, actionConfig);
				break;
			case "spawn_particles":
				spawnParticlesAction(player, actionConfig);
				break;
			case "modify_attribute":
				modifyAttributeAction(player, actionConfig);
				break;
			case "execute_command":
				executeCommandAction(player, actionConfig);
				break;
			case "teleport":
				teleportAction(player, actionConfig);
				break;
			case "ignite":
				ignite(player, target, actionConfig);
				break;
			case "damage":
				damageAction(ctx, actionConfig);
				break;
			default:
				LOGGER.warn("Unknown action: {}", action);
				break;
		}
	}

	/** Handles the actions that need both a player and the block they clicked. */
	private static void handleUseOnAction(InteractionContext ctx, String action, Map<String, Object> actionConfig) {
		switch (action) {
			case "convert_item" -> convertItem(ctx, actionConfig);
			default -> LOGGER.warn("Unknown use-on action: {}", action);
		}
	}

	private static void sendMessageAction(Player player, Map<String, Object> actionConfig) {
		String message = (String) actionConfig.get("message");
		if (message == null) {
			LOGGER.warn("send_message action is missing the required \"message\" value.");
			return;
		}
		// Defaults to the action bar, which is how this action has always behaved.
		if ((boolean) actionConfig.getOrDefault("overlay", true)) {
			player.sendOverlayMessage(Component.literal(message));
		} else {
			player.sendSystemMessage(Component.literal(message));
		}
	}

	private static void applyEffectAction(Player player, Map<String, Object> actionConfig) {
		String effect = (String) actionConfig.get("effect");
		int duration = ((Number) actionConfig.get("duration")).intValue();
		int amplifier = ((Number) actionConfig.get("amplifier")).intValue();
		player.addEffect(new MobEffectInstance(
				BuiltInRegistries.MOB_EFFECT.getOrThrow(ResourceKey.create(Registries.MOB_EFFECT, Identifier.parse(effect))),
				duration,
				amplifier
		));
	}

	private static void giveExperienceAction(Player player, Map<String, Object> actionConfig) {
		int amount = ((Number) actionConfig.getOrDefault("amount", DEFAULT_XP_AMOUNT)).intValue();
		player.giveExperiencePoints(amount);
	}

	private static void giveExperienceLevelsAction(Player player, Map<String, Object> actionConfig) {
		int amount = ((Number) actionConfig.getOrDefault("amount", DEFAULT_XP_AMOUNT)).intValue();
		player.giveExperienceLevels(amount);
	}

	private static void giveItemAction(Player player, Map<String, Object> actionConfig) {
		String itemId = (String) actionConfig.get("item");
		if (itemId == null) {
			LOGGER.warn("give_item action is missing the required \"item\" value.");
			return;
		}
		net.minecraft.world.item.Item given = BuiltInRegistries.ITEM.getValue(Identifier.tryParse(itemId));
		if (given == null) {
			LOGGER.warn("Unknown item \"{}\" in give_item action.", itemId);
			return;
		}
		int count = ((Number) actionConfig.getOrDefault("count", DEFAULT_ITEM_COUNT)).intValue();
		if (count <= 0) return;

		ItemStack stack = new ItemStack(given, count);
		if (!player.addItem(stack)) {
			player.drop(stack, false, Prediction.PREDICTED);
		}
	}

	/** Shrinks the stack the player is holding, i.e. the item the event fired for. */
	private static void consumeItemAction(InteractionContext ctx, Map<String, Object> actionConfig) {
		int count = ((Number) actionConfig.getOrDefault("count", DEFAULT_ITEM_COUNT)).intValue();
		if (count <= 0) return;
		ctx.heldStack().shrink(count);
	}

	/**
	 * Damages the stack the event is about — the worn one on an equipment event, the held one elsewhere.
	 * Does nothing for items without durability.
	 */
	private static void damageItemAction(InteractionContext ctx, Map<String, Object> actionConfig) {
		int amount = ((Number) actionConfig.getOrDefault("amount", DEFAULT_DURABILITY_LOSS)).intValue();
		if (amount <= 0) return;

		ItemStack stack = ctx.heldStack();
		if (!stack.has(DataComponents.MAX_DAMAGE)) return;
		stack.hurtAndBreak(amount, ctx.player(), ctx.equipmentSlot());
	}

	private static void setCooldownAction(InteractionContext ctx, Map<String, Object> actionConfig) {
		int seconds = ((Number) actionConfig.getOrDefault("seconds", DEFAULT_COOLDOWN)).intValue();
		if (seconds <= 0) return;
		ctx.player().getCooldowns().addCooldown(ctx.heldStack(), seconds * 20);
	}

	private static void healAction(Player player, Map<String, Object> actionConfig) {
		float amount = ((Number) actionConfig.getOrDefault("amount", 1)).floatValue();
		player.heal(amount);
	}

	private static void feedAction(Player player, Map<String, Object> actionConfig) {
		int food = ((Number) actionConfig.getOrDefault("food", 1)).intValue();
		float saturation = ((Number) actionConfig.getOrDefault("saturation", 0)).floatValue();
		player.getFoodData().eat(food, saturation);
	}

	private static void removeEffectAction(Player player, Map<String, Object> actionConfig) {
		String effect = (String) actionConfig.get("effect");
		if (effect == null) {
			LOGGER.warn("remove_effect action is missing the required \"effect\" value.");
			return;
		}
		player.removeEffect(BuiltInRegistries.MOB_EFFECT.getOrThrow(
				ResourceKey.create(Registries.MOB_EFFECT, Identifier.parse(effect))));
	}

	/**
	 * Hurts the target where the event supplies one, otherwise the player — the same subject rule as
	 * {@code ignite} and {@code extinguish}.
	 */
	private static void damageAction(InteractionContext ctx, Map<String, Object> actionConfig) {
		LivingEntity subject = ctx.subject();
		ServerLevel level = ctx.serverLevel();
		if (subject == null || level == null) return;

		float amount = ((Number) actionConfig.getOrDefault("amount", 1)).floatValue();
		if (amount <= 0) return;
		subject.hurt(level.damageSources().generic(), amount);
	}

	/** Extinguishes the target where the event supplies one, otherwise the player. */
	private static void extinguishAction(Player player, LivingEntity target) {
		LivingEntity subject = target != null ? target : player;
		if (subject == null) return;
		subject.clearFire();
	}

	private static void playSoundAction(Player player, Map<String, Object> actionConfig) {
		String soundId = (String) actionConfig.get("sound");
		if (soundId == null) {
			LOGGER.warn("play_sound action is missing the required \"sound\" value.");
			return;
		}
		SoundEvent soundEvent = BuiltInRegistries.SOUND_EVENT.getValue(Identifier.tryParse(soundId));
		if (soundEvent == null) {
			LOGGER.warn("Unknown sound \"{}\" in play_sound action.", soundId);
			return;
		}
		float volume = ((Number) actionConfig.getOrDefault("volume", DEFAULT_SOUND_VOLUME)).floatValue();
		float pitch = ((Number) actionConfig.getOrDefault("pitch", DEFAULT_SOUND_PITCH)).floatValue();
		player.level().playSound(player, player.getX(), player.getY(), player.getZ(), soundEvent, SoundSource.PLAYERS, volume, pitch);
	}

	private static void spawnParticlesAction(Player player, Map<String, Object> actionConfig) {
		String particleId = (String) actionConfig.get("particle");
		int count = ((Number) actionConfig.get("count")).intValue();
		double speedX = ((Number) actionConfig.get("speed")).doubleValue();
		double speedY = ((Number) actionConfig.get("speed")).doubleValue();
		double speedZ = ((Number) actionConfig.get("speed")).doubleValue();
		double offsetX = ((Number) actionConfig.get("offsetX")).doubleValue();
		double offsetY = ((Number) actionConfig.get("offsetY")).doubleValue();
		double offsetZ = ((Number) actionConfig.get("offsetZ")).doubleValue();
		// Note: ParticleRegistry should be defined to handle custom particles
		for (int i = 0; i < count; i++) {
			player.level().addParticle((ParticleOptions) BuiltInRegistries.PARTICLE_TYPE.getOrThrow(ResourceKey.create(Registries.PARTICLE_TYPE,
							Identifier.parse(particleId)
					)), player.getX() + offsetX + i, player.getY() + offsetY + i, player.getZ() + offsetZ + i,
					speedX + i, speedY + i, speedZ + i
			);
		}
	}

	private static void modifyAttributeAction(Player player, Map<String, Object> actionConfig) {
		// Example: modify speed attribute
		Identifier name = Identifier.tryParse((String) actionConfig.get("id"));
		String attribute = (String) actionConfig.get("attribute");
		double amount = ((Number) actionConfig.get("amount")).doubleValue();
		AttributeModifier.Operation operation = switch (((String) actionConfig.get("operation"))) {
			case "add_value" -> AttributeModifier.Operation.ADD_VALUE;
			case "add_multiplied_base" -> AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
			case "add_multiplied_total" -> AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL;
			default -> throw new IllegalStateException("Unexpected value: " + actionConfig.get("operation"));
		};
		player.getAttribute(BuiltInRegistries.ATTRIBUTE.getOrThrow(ResourceKey.create(Registries.ATTRIBUTE, Identifier.parse(attribute))))
				.addOrUpdateTransientModifier(new AttributeModifier(name, amount, operation));
	}

	private static void executeCommandAction(Player player, Map<String, Object> actionConfig) {
		executeCommand(player, (String) actionConfig.get("command"));
	}

	/**
	 * Runs a command as the given player, using that player's own permission level.
	 * Shared with the {@code run_command} shorthand in {@code useActions}.
	 */
	public static void executeCommand(Player player, String command) {
		if (command == null || command.isEmpty()) {
			LOGGER.warn("Tried to run a command action without a \"command\" value.");
			return;
		}
		if (player instanceof ServerPlayer serverPlayer) {
			serverPlayer.level().getServer().getCommands()
					.performPrefixedCommand(serverPlayer.createCommandSourceStack(), command);
		}
	}

	/**
	 * Spawns an entity at the event's position, offset by {@code x}/{@code y}/{@code z} and centred in the
	 * block. Server-side only, since nothing can be added to a client level.
	 */
	private static void spawnEntityAction(InteractionContext ctx, Map<String, Object> actionConfig) {
		ServerLevel level = ctx.serverLevel();
		if (level == null) return;

		String id = (String) actionConfig.get("entity");
		if (id == null) {
			LOGGER.warn("spawn_entity action is missing the required \"entity\" value.");
			return;
		}
		EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.tryParse(id));
		if (type == null) {
			LOGGER.warn("Unknown entity \"{}\" in spawn_entity action.", id);
			return;
		}

		// The action's own config doubles as a mob spec, so equipment, attributes, effects and a name
		// are written here exactly as they are in a world event's wave.
		MobSpec spec = readMobSpec(actionConfig);
		BlockPos at = offsetOf(ctx.pos(), actionConfig);
		int count = ((Number) actionConfig.getOrDefault("count", 1)).intValue();

		for (int i = 0; i < count; i++) {
			if (MobSpawner.spawn(level, at, type, spec) == null) return;
		}
	}

	/**
	 * Reads the mob-shaping half of an action's config.
	 *
	 * <p>Round-tripped through Gson rather than read field by field, so the spec is described in exactly
	 * one place and an action gains whatever a wave gains.
	 */
	private static MobSpec readMobSpec(Map<String, Object> actionConfig) {
		try {
			MobSpec spec = BaseGson.GSON.fromJson(BaseGson.GSON.toJsonTree(actionConfig), MobSpec.class);
			return spec == null ? new MobSpec() : spec;
		} catch (Exception e) {
			LOGGER.warn("Could not read the mob settings on a spawn_entity action.", e);
			return new MobSpec();
		}
	}

	/** Drops an item in the world, as opposed to {@code give_item}, which needs a player to give it to. */
	private static void dropItemAction(InteractionContext ctx, Map<String, Object> actionConfig) {
		String id = (String) actionConfig.get("item");
		if (id == null) {
			LOGGER.warn("drop_item action is missing the required \"item\" value.");
			return;
		}
		net.minecraft.world.item.Item item = BuiltInRegistries.ITEM.getValue(Identifier.tryParse(id));
		if (item == null) {
			LOGGER.warn("Unknown item \"{}\" in drop_item action.", id);
			return;
		}

		int count = ((Number) actionConfig.getOrDefault("count", DEFAULT_ITEM_COUNT)).intValue();
		if (count <= 0) return;
		Block.popResource(ctx.level(), offsetOf(ctx.pos(), actionConfig), new ItemStack(item, count));
	}

	/** Particles at the block rather than at the player, the counterpart to {@code play_sound_at}. */
	private static void spawnParticlesAtAction(InteractionContext ctx, Map<String, Object> actionConfig) {
		ServerLevel level = ctx.serverLevel();
		if (level == null) return;

		String id = (String) actionConfig.get("particle");
		if (id == null) {
			LOGGER.warn("spawn_particles_at action is missing the required \"particle\" value.");
			return;
		}

		ParticleOptions particle = (ParticleOptions) BuiltInRegistries.PARTICLE_TYPE.getOrThrow(
				ResourceKey.create(Registries.PARTICLE_TYPE, Identifier.parse(id)));
		Vec3 at = Vec3.atCenterOf(offsetOf(ctx.pos(), actionConfig));
		int count = ((Number) actionConfig.getOrDefault("count", DEFAULT_PARTICLE_COUNT)).intValue();
		double spread = ((Number) actionConfig.getOrDefault("spread", 0.5)).doubleValue();
		double speed = ((Number) actionConfig.getOrDefault("speed", 0.0)).doubleValue();

		level.sendParticles(particle, at.x, at.y, at.z, count, spread, spread, spread, speed);
	}

	private static void strikeLightningAction(InteractionContext ctx, Map<String, Object> actionConfig) {
		ServerLevel level = ctx.serverLevel();
		if (level == null) return;

		LightningBolt bolt = EntityTypes.LIGHTNING_BOLT.create(level, EntitySpawnReason.SPAWN_ITEM_USE);
		if (bolt == null) return;

		Vec3 at = Vec3.atBottomCenterOf(offsetOf(ctx.pos(), actionConfig));
		bolt.moveOrInterpolateTo(PositionPath.of(at), 0.0F, 0.0F);
		bolt.setVisualOnly((boolean) actionConfig.getOrDefault("visual_only", false));
		level.addFreshEntity(bolt);
	}

	/** The event's position shifted by the action's {@code x}/{@code y}/{@code z}, each defaulting to 0. */
	private static BlockPos offsetOf(BlockPos pos, Map<String, Object> actionConfig) {
		return pos.offset(
				((Number) actionConfig.getOrDefault("x", 0)).intValue(),
				((Number) actionConfig.getOrDefault("y", 0)).intValue(),
				((Number) actionConfig.getOrDefault("z", 0)).intValue());
	}

	/**
	 * Opens a crate menu. The config comes from the action's own {@code menu} object when it has one, and
	 * otherwise from the {@code menu_config} of the block that was clicked — which is where a crate block
	 * keeps its rewards, so the key that opens it carries no loot of its own.
	 *
	 * <p>Only an inline {@code menu} may consume the held stack, and only when its pool says so: that is the
	 * crate item eating itself. A crate read off a block never touches what the player is holding, so a key
	 * is taken with a {@code has_item} condition carrying {@code consume} instead.
	 */
	private static void openCrateAction(InteractionContext ctx, Map<String, Object> actionConfig) {
		Object inline = actionConfig.get("menu");
		if (inline != null) {
			CustomMenuConfig config = BaseGson.GSON.fromJson(BaseGson.GSON.toJsonTree(inline), CustomMenuConfig.class);
			if (config == null) {
				LOGGER.warn("open_crate could not read its \"menu\" object: {}", inline);
				return;
			}
			CrateMenus.open(ctx.player(), config, ctx.heldStack());
			return;
		}

		BlockState state = ctx.blockState();
		if (state == null) {
			LOGGER.warn("open_crate needs either a \"menu\" object or a block to read one from.");
			return;
		}

		io.github.vampirestudios.obsidian.api.obsidian.block.Block block =
				ContentRegistries.BLOCKS.getValue(BuiltInRegistries.BLOCK.getKey(state.getBlock()));
		if (block == null || block.menuConfig == null) {
			LOGGER.warn("open_crate found no \"menu_config\" on the block at {}.", ctx.pos());
			return;
		}
		CrateMenus.open(ctx.player(), block.menuConfig, ItemStack.EMPTY);
	}

	private static void openGuiAction(Player player, Map<String, Object> actionConfig) {
		if (player.level().isClientSide()) return;

		String type = (String) actionConfig.get("gui_type");
		if (type == null) {
			LOGGER.warn("open_gui action is missing the required \"gui_type\" value.");
			return;
		}

		UseActions.GuiType guiType;
		try {
			guiType = UseActions.GuiType.valueOf(type.toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException e) {
			LOGGER.warn("Unknown gui_type \"{}\" in open_gui action.", type);
			return;
		}

		int guiSize = ((Number) actionConfig.getOrDefault("gui_size", DEFAULT_GUI_SIZE)).intValue();
		String title = (String) actionConfig.get("title");
		player.openMenu(UseActions.createMenuProvider(
				guiType,
				guiSize,
				title != null ? Component.literal(title) : Component.empty(),
				ContainerLevelAccess.create(player.level(), player.blockPosition())
		));
	}

	private static void teleportAction(Player player, Map<String, Object> actionConfig) {
		double x = ((Number) actionConfig.get("x")).doubleValue();
		double y = ((Number) actionConfig.get("y")).doubleValue();
		double z = ((Number) actionConfig.get("z")).doubleValue();
		player.teleportTo(x, y, z);
	}

	private static void ignite(Player player, LivingEntity target, Map<String, Object> actionConfig) {
		// Events that carry a target (hurt_enemy, interact_entity) ignite it; the rest ignite the player.
		LivingEntity subject = target != null ? target : player;
		if (subject == null) return;

		int duration = ((Number) actionConfig.getOrDefault("duration_in_seconds", 1)).intValue();
		subject.igniteForSeconds(duration);
	}

	private static void convertItem(InteractionContext ctx, Map<String, Object> actionConfig) {
		Player player = ctx.player();
		Level level = ctx.level();
		BlockPos pos = ctx.pos();
		BlockState state = ctx.blockState();

		Optional<Block> optionalBlock = Optional.ofNullable((String) actionConfig.get("block"))
				.map(id -> BuiltInRegistries.BLOCK.getValue(Identifier.tryParse(id)));
		Block block = optionalBlock.orElse(null);

		if (block == null || state.is(block)) {
			InteractionHand hand = ctx.hand() != null ? ctx.hand() : InteractionHand.MAIN_HAND;
			processItemConversion(player, level, pos, hand, actionConfig, block != null);
		} else {
			LOGGER.info("Block does not match for conversion at position: {}", pos);
		}
	}

	private static void processItemConversion(Player player, Level level, BlockPos pos, InteractionHand hand, Map<String, Object> actionConfig, boolean playEffects) {
		ItemStack stack = player.getItemInHand(hand);

		int requiredAmount = ((Number) actionConfig.getOrDefault("amount_required", DEFAULT_AMOUNT_REQUIRED)).intValue();
		if (stack.getCount() < requiredAmount) {
			LOGGER.info("Insufficient items for conversion. Required: {}, Available: {}", requiredAmount, stack.getCount());
			return;
		}

		if (!checkCooldown(player, actionConfig)) {
			LOGGER.info("Player {} is on cooldown for conversion.", player.getName().getString());
			return;
		}

		String newItemId = (String) actionConfig.get("converted_item");
		if (newItemId == null) {
			LOGGER.warn("Converted item ID is null in actionConfig.");
			return;
		}

		ItemStack newItem = new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.tryParse(newItemId)));

		if (playEffects) {
			if (level.isClientSide()) {
				spawnParticles(level, pos, stack, actionConfig);
				player.swing(hand, stack.getInteractAnimation(), true);
			}
			playSound(level, pos, actionConfig);
		}

		player.startUsingItem(hand);
		player.releaseUsingItem();

		stack.shrink(requiredAmount);
		if (!player.addItem(newItem)) {
			player.drop(newItem, false, Prediction.PREDICTED);
		}

		displayCustomMessage(player, actionConfig);
		applyCustomDurability(stack, player, hand, actionConfig);
		giveExperience(player, actionConfig);
		setCooldown(player, actionConfig);
	}

	private static void spawnParticles(Level level, BlockPos pos, ItemStack stack, Map<String, Object> actionConfig) {
		ItemStack particleItem = Optional.ofNullable((String) actionConfig.get("particle_item"))
				.map(id -> new ItemStack(BuiltInRegistries.ITEM.getValue(Identifier.parse(id))))
				.orElse(stack);
		int count = ((Number) actionConfig.getOrDefault("max_particle_count", DEFAULT_PARTICLE_COUNT)).intValue();
		ParticleUtils.spawnParticlesOnBlockFaces(level, pos, new ItemParticleOption(ParticleTypes.ITEM, particleItem.getItem()), UniformInt.of(1, count));
	}

	private static void playSound(Level level, BlockPos pos, Map<String, Object> actionConfig) {
		SoundEvent soundEvent = Optional.ofNullable((String) actionConfig.get("sound"))
				.map(id -> BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse(id)))
				.orElse(SoundEvents.GRINDSTONE_USE);
		float volume = ((Number) actionConfig.getOrDefault("sound_volume", DEFAULT_SOUND_VOLUME)).floatValue();
		float pitch = ((Number) actionConfig.getOrDefault("sound_pitch", DEFAULT_SOUND_PITCH)).floatValue();
		level.playSound(null, pos, soundEvent, SoundSource.BLOCKS, volume, pitch);
	}

	private static void displayCustomMessage(Player player, Map<String, Object> actionConfig) {
		String message = (String) actionConfig.get("message");
		if (message != null) {
			player.sendOverlayMessage(Component.literal(message));
		}
	}

	private static void applyCustomDurability(ItemStack stack, Player player, InteractionHand hand, Map<String, Object> actionConfig) {
		int durabilityLoss = ((Number) actionConfig.getOrDefault("durability_loss", 0)).intValue();
		if (durabilityLoss > 0 && stack.has(DataComponents.MAX_DAMAGE)) {
			stack.hurtAndBreak(durabilityLoss, player, hand.asEquipmentSlot());
		}
	}

	private static boolean checkCooldown(Player player, Map<String, Object> actionConfig) {
		int cooldown = ((Number) actionConfig.getOrDefault("cooldown", DEFAULT_COOLDOWN)).intValue();
		return cooldown <= 0 || !player.getCooldowns().isOnCooldown(player.getItemInHand(InteractionHand.MAIN_HAND));
	}

	private static void setCooldown(Player player, Map<String, Object> actionConfig) {
		int cooldown = ((Number) actionConfig.getOrDefault("cooldown", DEFAULT_COOLDOWN)).intValue();
		if (cooldown > 0) {
			player.getCooldowns().addCooldown(player.getItemInHand(InteractionHand.MAIN_HAND), cooldown * 20);
		}
	}

	private static void giveExperience(Player player, Map<String, Object> actionConfig) {
		int xpAmount = ((Number) actionConfig.getOrDefault("xp_amount", DEFAULT_XP_AMOUNT)).intValue();
		XPType xpType = XPType.valueOf((String) actionConfig.getOrDefault("xp_type", XPType.POINTS.name().toLowerCase(Locale.ROOT)));
		if (xpAmount > 0) {
			switch (xpType) {
				case POINTS -> player.giveExperiencePoints(xpAmount);
				case LEVELS -> player.giveExperienceLevels(xpAmount);
			}
		}
	}

	private enum XPType {POINTS, LEVELS}

    /*private static void unlockRecipeAction(Player player, Map<String, Object> actionConfig) {
        if (player instanceof ServerPlayer serverPlayer) {
            String recipeId = (String) actionConfig.get("recipe");
            Recipe<?> recipe = serverPlayer.server.getRecipeManager().getRecipe(Identifier.tryParse(recipeId)).orElse(null);
            if (recipe != null) {
                serverPlayer.awardRecipes(List.of(recipe));
            }
        }
    }*/
}
