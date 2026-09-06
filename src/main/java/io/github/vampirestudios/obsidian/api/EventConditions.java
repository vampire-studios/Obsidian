package io.github.vampirestudios.obsidian.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * The optional {@code conditions} list on an event action: a set of checks that all have to pass before
 * the action runs.
 *
 * <p>Conditions double as requirements. A condition with {@code consume} takes what it checked for, which
 * is how a key or keycard is expressed — but only once every condition in the list has passed, so a
 * failing second condition cannot eat the key. Consumption is collected while evaluating and applied at
 * the end, which is also what makes {@code any_of} able to accept either of two keys and take only the
 * one the player actually had.
 *
 * <p>Unlike unknown <em>actions</em>, which are skipped with a warning while the rest of the list runs, an
 * unknown or malformed <em>condition</em> fails the whole list. A gate that cannot be understood must stay
 * shut: a typo in a lock should never open the door.
 */
public final class EventConditions {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final RandomSource RANDOM = RandomSource.create();

	private static final String WHERE_HAND = "hand";
	private static final String WHERE_INVENTORY = "inventory";
	private static final String WHERE_ARMOR = "armor";

	private static final EquipmentSlot[] ARMOR_SLOTS = {
			EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
	};

	private static final long DAY_LENGTH = 24000L;

	// The ticks vanilla's clock markers sit on. Kept as constants rather than read back off the clock
	// so the condition still resolves in a dimension whose clock declares no markers.
	private static final long MARKER_DAY = 1000L;
	private static final long MARKER_NOON = 6000L;
	private static final long MARKER_NIGHT = 13000L;
	private static final long MARKER_MIDNIGHT = 18000L;

	/** Moon phases in the order the game cycles them, starting from the full moon on day 0. */
	private static final String[] MOON_PHASES = {
			"full", "waning_gibbous", "last_quarter", "waning_crescent",
			"new", "waxing_crescent", "first_quarter", "waxing_gibbous"
	};

	private EventConditions() {
	}

	/** The outcome of a condition: whether it passed, and what should be taken if the whole list passes. */
	private record Result(boolean passed, List<Map<String, Object>> consumables) {
		static final Result FAIL = new Result(false, List.of());
		static final Result PASS = new Result(true, List.of());

		static Result of(boolean passed) {
			return passed ? PASS : FAIL;
		}
	}

	/**
	 * Tests every condition on the action, and applies the consuming ones only if all of them passed.
	 * An action with no {@code conditions} key always passes.
	 */
	public static boolean pass(InteractionContext ctx, Map<String, Object> actionConfig) {
		Object raw = actionConfig.get("conditions");
		if (raw == null) return true;

		Result result = testAll(ctx, raw);
		if (!result.passed()) return false;

		for (Map<String, Object> condition : result.consumables()) consume(ctx, condition);
		return true;
	}

	/** Every condition in a list has to pass; their consumables are pooled. */
	private static Result testAll(InteractionContext ctx, Object raw) {
		List<Map<String, Object>> conditions = asConditionList(raw);
		if (conditions == null) return Result.FAIL;

		List<Map<String, Object>> consumables = new ArrayList<>();
		for (Map<String, Object> condition : conditions) {
			Result result = evaluate(ctx, condition);
			if (!result.passed()) {
				sendFailureMessage(ctx, condition);
				return Result.FAIL;
			}
			consumables.addAll(result.consumables());
		}
		return new Result(true, consumables);
	}

	/** One condition, with {@code invert} applied and {@code consume} recorded. */
	private static Result evaluate(InteractionContext ctx, Map<String, Object> condition) {
		String name = (String) condition.get("condition");
		if (name == null) {
			LOGGER.warn("Condition is missing the required \"condition\" key: {}", condition);
			return Result.FAIL;
		}

		Result result;
		try {
			result = test(ctx, name, condition);
		} catch (Exception e) {
			LOGGER.error("Failed to test condition: {}", name, e);
			return Result.FAIL;
		}

		boolean inverted = Boolean.TRUE.equals(condition.get("invert"));
		if (!inverted) return result;

		// An inverted condition passes because the thing was NOT there, so there is nothing to take.
		if (!result.consumables().isEmpty()) {
			LOGGER.warn("Condition \"{}\" cannot both invert and consume; the consume is ignored.", name);
		}
		return Result.of(!result.passed());
	}

	private static Result test(InteractionContext ctx, String name, Map<String, Object> condition) {
		return switch (name) {
			// -- logic -----------------------------------------------------------------------------
			case "all_of" -> testAll(ctx, condition.get("conditions"));
			case "any_of" -> anyOf(ctx, condition);
			case "none_of" -> noneOf(ctx, condition);

			// -- items -----------------------------------------------------------------------------
			case "has_item" -> hasItem(ctx, condition);
			case "has_component" -> Result.of(hasComponent(ctx, condition));
			case "item_damage" -> Result.of(ctx.player() != null && inRange(ctx.heldStack().getDamageValue(), condition));
			case "item_count" -> Result.of(ctx.player() != null && inRange(ctx.heldStack().getCount(), condition));
			case "durability_percent" -> Result.of(ctx.player() != null && inRange(durabilityPercent(ctx.heldStack()), condition));
			case "enchantment" -> Result.of(enchantment(ctx, condition));

			// -- the player ------------------------------------------------------------------------
			case "is_sneaking" -> Result.of(playerState(ctx, "sneaking"));
			case "player_state" -> Result.of(playerState(ctx, (String) condition.get("state")));
			case "health" -> Result.of(ctx.player() != null && inRange(ctx.player().getHealth(), condition));
			case "food_level" -> Result.of(ctx.player() != null && inRange(ctx.player().getFoodData().getFoodLevel(), condition));
			case "experience_level" -> Result.of(ctx.player() != null && inRange(ctx.player().experienceLevel, condition));
			case "has_effect" -> Result.of(hasEffect(ctx, condition));
			case "gamemode" -> Result.of(gamemode(ctx, condition));
			case "riding" -> Result.of(riding(ctx, condition));

			// -- the block -------------------------------------------------------------------------
			case "block_is" -> Result.of(blockIs(ctx, ctx.pos(), condition));
			case "block_has_tag" -> Result.of(blockHasTag(ctx, ctx.pos(), condition));
			case "block_property" -> Result.of(blockProperty(ctx, condition));
			case "block_at_offset" -> Result.of(blockAtOffset(ctx, condition));
			case "standing_on" -> Result.of(standingOn(ctx, condition));

			// -- the world -------------------------------------------------------------------------
			case "dimension" -> Result.of(dimension(ctx, condition));
			case "biome" -> Result.of(biome(ctx, condition));
			case "time_of_day" -> Result.of(timeOfDay(ctx, condition));
			case "day_count" -> Result.of(inRange(dayCount(ctx.level()), condition));
			case "moon_phase" -> Result.of(moonPhase(ctx, condition));
			case "weather" -> Result.of(weather(ctx, condition));
			case "light_level" -> Result.of(lightLevel(ctx, condition));
			case "height" -> Result.of(ctx.referencePos() != null && inRange(ctx.referencePos().getY(), condition));
			case "can_see_sky" -> Result.of(ctx.referencePos() != null && ctx.level().canSeeSky(ctx.referencePos()));

			// -- the target entity -----------------------------------------------------------------
			case "entity_is" -> Result.of(entityIs(ctx, condition));
			case "entity_health" -> Result.of(ctx.target() != null && inRange(ctx.target().getHealth(), condition));
			case "entity_is_baby" -> Result.of(ctx.target() != null && ctx.target().isBaby());

			// -- misc ------------------------------------------------------------------------------
			case "chance" -> Result.of(RANDOM.nextDouble() < ((Number) condition.getOrDefault("value", 1.0)).doubleValue());

			default -> {
				LOGGER.warn("Unknown condition: {}", name);
				yield Result.FAIL;
			}
		};
	}

	// -- logic combinators -----------------------------------------------------------------------

	/**
	 * Passes as soon as one branch does, taking only that branch's consumables — so "either keycard opens
	 * this" takes the card the player actually had, not both.
	 */
	private static Result anyOf(InteractionContext ctx, Map<String, Object> condition) {
		List<Map<String, Object>> branches = asConditionList(condition.get("conditions"));
		if (branches == null) return Result.FAIL;

		for (Map<String, Object> branch : branches) {
			Result result = evaluate(ctx, branch);
			if (result.passed()) return result;
		}
		return Result.FAIL;
	}

	/** Passes when no branch does. Nothing is consumed, since nothing matched. */
	private static Result noneOf(InteractionContext ctx, Map<String, Object> condition) {
		List<Map<String, Object>> branches = asConditionList(condition.get("conditions"));
		if (branches == null) return Result.FAIL;

		for (Map<String, Object> branch : branches) {
			if (evaluate(ctx, branch).passed()) return Result.FAIL;
		}
		return Result.PASS;
	}

	// -- item conditions -------------------------------------------------------------------------

	/**
	 * Whether the player has enough matching items, by {@code item} id or {@code tag}, in the hand or
	 * across the inventory. This is the condition that {@code consume} is for.
	 */
	private static Result hasItem(InteractionContext ctx, Map<String, Object> condition) {
		Player player = ctx.player();
		if (player == null) {
			LOGGER.warn("Condition \"has_item\" needs a player, but this event does not have one.");
			return Result.FAIL;
		}

		int required = requiredCount(condition);
		int found = 0;
		for (ItemStack stack : stacksFor(ctx, player, condition)) {
			if (matches(stack, condition)) found += stack.getCount();
			if (found >= required) break;
		}

		if (found < required) return Result.FAIL;
		return Boolean.TRUE.equals(condition.get("consume"))
				? new Result(true, List.of(condition))
				: Result.PASS;
	}

	private static boolean matches(ItemStack stack, Map<String, Object> condition) {
		if (stack.isEmpty()) return false;

		String tag = (String) condition.get("tag");
		if (tag != null) {
			return stack.is(TagKey.create(Registries.ITEM, Identifier.parse(tag)));
		}

		String itemId = (String) condition.get("item");
		if (itemId == null) {
			LOGGER.warn("Condition \"has_item\" needs either an \"item\" or a \"tag\".");
			return false;
		}
		return BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(Identifier.tryParse(itemId));
	}

	/**
	 * Whether the held stack carries a data component. Presence only — this does not compare the
	 * component's value, so tiers are best expressed as separate items or an item tag for now.
	 */
	private static boolean hasComponent(InteractionContext ctx, Map<String, Object> condition) {
		String id = (String) condition.get("component");
		if (id == null) {
			LOGGER.warn("Condition \"has_component\" is missing the required \"component\" value.");
			return false;
		}

		DataComponentType<?> type = BuiltInRegistries.DATA_COMPONENT_TYPE.getValue(Identifier.tryParse(id));
		if (type == null) {
			LOGGER.warn("Unknown component \"{}\" in has_component condition.", id);
			return false;
		}
		return ctx.heldStack().has(type);
	}

	// -- player conditions -----------------------------------------------------------------------

	private static boolean playerState(InteractionContext ctx, String state) {
		Player player = ctx.player();
		if (player == null) {
			LOGGER.warn("Player state conditions need a player, but this event does not have one.");
			return false;
		}
		if (state == null) {
			LOGGER.warn("Condition \"player_state\" is missing the required \"state\" value.");
			return false;
		}

		return switch (state.toLowerCase(Locale.ROOT)) {
			case "sneaking" -> player.isShiftKeyDown();
			case "sprinting" -> player.isSprinting();
			case "swimming" -> player.isSwimming();
			case "underwater" -> player.isUnderWater();
			case "in_water" -> player.isInWater();
			case "in_lava" -> player.isInLava();
			case "on_ground" -> player.onGround();
			case "flying" -> player.getAbilities().flying;
			case "may_fly" -> player.getAbilities().mayfly;
			case "burning" -> player.isOnFire();
			case "creative" -> player.isCreative();
			case "spectator" -> player.isSpectator();
			case "alive" -> player.isAlive();
			case "sleeping" -> player.isSleeping();
			case "using_item" -> player.isUsingItem();
			default -> {
				LOGGER.warn("Unknown player state: {}", state);
				yield false;
			}
		};
	}

	private static boolean hasEffect(InteractionContext ctx, Map<String, Object> condition) {
		Player player = ctx.player();
		String effect = (String) condition.get("effect");
		if (player == null || effect == null) {
			LOGGER.warn("Condition \"has_effect\" needs a player and an \"effect\" value.");
			return false;
		}

		MobEffectInstance instance = player.getEffect(BuiltInRegistries.MOB_EFFECT.getOrThrow(
				ResourceKey.create(Registries.MOB_EFFECT, Identifier.parse(effect))));
		if (instance == null) return false;

		Object minAmplifier = condition.get("min_amplifier");
		return minAmplifier == null || instance.getAmplifier() >= ((Number) minAmplifier).intValue();
	}

	/** One game mode name, or a list of them. */
	private static boolean gamemode(InteractionContext ctx, Map<String, Object> condition) {
		if (!(ctx.player() instanceof ServerPlayer player)) {
			LOGGER.warn("Condition \"gamemode\" needs a server player, but this event does not have one.");
			return false;
		}

		String actual = player.gameMode.getGameModeForPlayer().getName();
		for (String name : ids(condition, "value", "values")) {
			if (name.equalsIgnoreCase(actual)) return true;
		}
		return false;
	}

	/**
	 * Whether the player is riding something, and optionally what. Naming no entity matches any
	 * vehicle, which includes sitting on a block — those are {@code obsidian:seat}.
	 */
	private static boolean riding(InteractionContext ctx, Map<String, Object> condition) {
		Player player = ctx.player();
		if (player == null) {
			LOGGER.warn("Condition \"riding\" needs a player, but this event does not have one.");
			return false;
		}

		Entity vehicle = player.getVehicle();
		if (vehicle == null) return false;
		if (condition.get("entity") == null && condition.get("entities") == null) return true;

		Identifier actual = BuiltInRegistries.ENTITY_TYPE.getKey(vehicle.getType());
		for (String id : ids(condition, "entity", "entities")) {
			if (actual.equals(Identifier.tryParse(id))) return true;
		}
		return false;
	}

	// -- block conditions ------------------------------------------------------------------------

	private static boolean blockIs(InteractionContext ctx, BlockPos pos, Map<String, Object> condition) {
		if (pos == null) {
			LOGGER.warn("Condition \"block_is\" needs a block position, but this event does not have one.");
			return false;
		}

		Identifier actual = BuiltInRegistries.BLOCK.getKey(ctx.level().getBlockState(pos).getBlock());
		for (String id : ids(condition, "block", "blocks")) {
			if (actual.equals(Identifier.tryParse(id))) return true;
		}
		return false;
	}

	private static boolean blockHasTag(InteractionContext ctx, BlockPos pos, Map<String, Object> condition) {
		if (pos == null) {
			LOGGER.warn("Condition \"block_has_tag\" needs a block position, but this event does not have one.");
			return false;
		}

		BlockState state = ctx.level().getBlockState(pos);
		for (String tag : ids(condition, "tag", "tags")) {
			if (state.is(TagKey.create(Registries.BLOCK, Identifier.parse(tag)))) return true;
		}
		return false;
	}

	/** Compares one state property of the clicked block, by the property's own value names. */
	private static boolean blockProperty(InteractionContext ctx, Map<String, Object> condition) {
		BlockState state = ctx.blockState();
		String name = (String) condition.get("property");
		if (state == null || name == null) {
			LOGGER.warn("Condition \"block_property\" needs a block position and a \"property\" value.");
			return false;
		}

		Property<?> property = state.getBlock().getStateDefinition().getProperty(name);
		if (property == null) {
			LOGGER.warn("Block at {} has no property \"{}\".", ctx.pos(), name);
			return false;
		}
		return String.valueOf(condition.get("value")).equalsIgnoreCase(valueName(state, property));
	}

	private static <T extends Comparable<T>> String valueName(BlockState state, Property<T> property) {
		return property.getName(state.getValue(property));
	}

	/**
	 * Checks a block relative to the one that fired the event. Offsets do not rotate with the block, unlike
	 * the positional actions — this asks about the world as it is, not about a layout.
	 */
	/**
	 * The block underfoot, which {@code block_at_offset} cannot reach — its offsets are measured from
	 * the event's block, not from the player. Takes an {@code item}-style {@code block}/{@code blocks},
	 * or a {@code tag}/{@code tags}.
	 */
	private static boolean standingOn(InteractionContext ctx, Map<String, Object> condition) {
		Player player = ctx.player();
		if (player == null) {
			LOGGER.warn("Condition \"standing_on\" needs a player, but this event does not have one.");
			return false;
		}

		BlockPos below = player.blockPosition().below();
		boolean byTag = condition.get("tag") != null || condition.get("tags") != null;
		return byTag ? blockHasTag(ctx, below, condition) : blockIs(ctx, below, condition);
	}

	private static boolean blockAtOffset(InteractionContext ctx, Map<String, Object> condition) {
		BlockPos pos = ctx.pos();
		if (pos == null) {
			LOGGER.warn("Condition \"block_at_offset\" needs a block position, but this event does not have one.");
			return false;
		}

		BlockPos at = pos.offset(
				((Number) condition.getOrDefault("x", 0)).intValue(),
				((Number) condition.getOrDefault("y", 0)).intValue(),
				((Number) condition.getOrDefault("z", 0)).intValue());
		return condition.get("tag") != null || condition.get("tags") != null
				? blockHasTag(ctx, at, condition)
				: blockIs(ctx, at, condition);
	}

	// -- world conditions ------------------------------------------------------------------------

	private static boolean dimension(InteractionContext ctx, Map<String, Object> condition) {
		Identifier actual = ctx.level().dimension().identifier();
		for (String id : ids(condition, "dimension", "dimensions")) {
			if (actual.equals(Identifier.tryParse(id))) return true;
		}
		return false;
	}

	private static boolean biome(InteractionContext ctx, Map<String, Object> condition) {
		BlockPos pos = ctx.referencePos();
		if (pos == null) return false;

		var holder = ctx.level().getBiome(pos);
		if (condition.get("tag") != null || condition.get("tags") != null) {
			for (String tag : ids(condition, "tag", "tags")) {
				if (holder.is(TagKey.create(Registries.BIOME, Identifier.parse(tag)))) return true;
			}
			return false;
		}

		ResourceKey<?> key = holder.unwrapKey().orElse(null);
		if (key == null) return false;
		for (String id : ids(condition, "biome", "biomes")) {
			if (key.identifier().equals(Identifier.tryParse(id))) return true;
		}
		return false;
	}

	/**
	 * Either a named span — {@code "value": "day"} or {@code "night"} — or an explicit {@code min}/{@code max}
	 * in ticks within the 24000-tick day.
	 */
	private static boolean timeOfDay(InteractionContext ctx, Map<String, Object> condition) {
		String value = (String) condition.get("value");
		if (value == null) return inRange(dayTime(ctx.level()), condition);

		return switch (value.toLowerCase(Locale.ROOT)) {
			// Each named span runs from its clock marker to the next one.
			case "day" -> between(dayTime(ctx.level()), MARKER_DAY, MARKER_NOON);
			case "noon" -> between(dayTime(ctx.level()), MARKER_NOON, MARKER_NIGHT);
			case "night" -> between(dayTime(ctx.level()), MARKER_NIGHT, MARKER_MIDNIGHT);
			case "midnight" -> between(dayTime(ctx.level()), MARKER_MIDNIGHT, MARKER_DAY);

			// Whether it is light out is the dimension's answer, not a tick comparison's.
			case "bright_outside" -> ctx.level().isBrightOutside();
			case "dark_outside" -> ctx.level().isDarkOutside();

			default -> {
				LOGGER.warn("Condition \"time_of_day\" value must be one of \"day\", \"noon\", \"night\", "
						+ "\"midnight\", \"bright_outside\" or \"dark_outside\", but was: {}", value);
				yield false;
			}
		};
	}

	/** The tick within the current day. */
	private static long dayTime(Level level) {
		return level.getOverworldClockTime() % DAY_LENGTH;
	}

	/** Days elapsed since the world was created, the same number the F3 screen shows. */
	private static long dayCount(Level level) {
		return level.getOverworldClockTime() / DAY_LENGTH;
	}

	/**
	 * Either a named phase or a number 0–7, where 0 is the full moon. Derived from the day count the
	 * same way the sky is, so it matches what the player can see.
	 */
	private static boolean moonPhase(InteractionContext ctx, Map<String, Object> condition) {
		int phase = (int) (dayCount(ctx.level()) % MOON_PHASES.length);

		String value = (String) condition.get("value");
		if (value == null) return inRange(phase, condition);

		String named = value.toLowerCase(Locale.ROOT);
		for (int i = 0; i < MOON_PHASES.length; i++) {
			if (MOON_PHASES[i].equals(named)) return phase == i;
		}

		LOGGER.warn("Condition \"moon_phase\" value must be a number 0-7 or one of {}, but was: {}",
				String.join(", ", MOON_PHASES), value);
		return false;
	}

	/** Half-open span between two clock markers, wrapping past midnight when {@code from} is the later one. */
	private static boolean between(long time, long from, long to) {
		return from <= to ? time >= from && time < to : time >= from || time < to;
	}

	/**
	 * World weather by default. With {@code "local": true} it asks whether the weather actually reaches
	 * this spot instead — a desert biome or anything with a roof over it never counts as raining.
	 */
	private static boolean weather(InteractionContext ctx, Map<String, Object> condition) {
		Level level = ctx.level();
		String value = (String) condition.getOrDefault("value", "clear");

		boolean local = Boolean.TRUE.equals(condition.get("local"));
		BlockPos pos = ctx.referencePos();
		if (local && pos == null) {
			LOGGER.warn("Condition \"weather\" with \"local\" needs a position, but this event does not have one.");
			return false;
		}

		boolean raining = local ? level.isRainingAt(pos) : level.isRaining();

		return switch (value.toLowerCase(Locale.ROOT)) {
			case "clear" -> !raining;
			case "raining" -> raining;
			// Thunder is a world-wide state, so locally it means "storming, and it reaches here".
			case "thundering" -> level.isThundering() && (!local || raining);
			default -> {
				LOGGER.warn("Condition \"weather\" value must be clear, raining or thundering, but was: {}", value);
				yield false;
			}
		};
	}

	/**
	 * Light at the position. {@code type} picks which light: {@code block} for torches and lava,
	 * {@code sky} for daylight reaching the spot, or {@code any} — the default — for whichever is
	 * brighter, which is what mob spawning cares about.
	 */
	private static boolean lightLevel(InteractionContext ctx, Map<String, Object> condition) {
		BlockPos pos = ctx.referencePos();
		if (pos == null) return false;

		String type = ((String) condition.getOrDefault("type", "any")).toLowerCase(Locale.ROOT);

		return switch (type) {
			case "any" -> inRange(ctx.level().getMaxLocalRawBrightness(pos), condition);
			case "block" -> inRange(ctx.level().getBrightness(LightLayer.BLOCK, pos), condition);
			case "sky" -> inRange(ctx.level().getBrightness(LightLayer.SKY, pos), condition);
			default -> {
				LOGGER.warn("Condition \"light_level\" type must be any, block or sky, but was: {}", type);
				yield false;
			}
		};
	}

	// -- entity conditions -----------------------------------------------------------------------

	private static boolean entityIs(InteractionContext ctx, Map<String, Object> condition) {
		LivingEntity target = ctx.target();
		if (target == null) {
			LOGGER.warn("Condition \"entity_is\" needs a target, but this event does not have one.");
			return false;
		}

		if (condition.get("tag") != null || condition.get("tags") != null) {
			for (String tag : ids(condition, "tag", "tags")) {
				if (target.is(TagKey.create(Registries.ENTITY_TYPE, Identifier.parse(tag)))) return true;
			}
			return false;
		}

		Identifier actual = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());
		for (String id : ids(condition, "entity", "entities")) {
			if (actual.equals(Identifier.tryParse(id))) return true;
		}
		return false;
	}

	// -- consumption and feedback ------------------------------------------------------------------

	/** Takes what a passing {@code consume} condition checked for. Creative players are never charged. */
	private static void consume(InteractionContext ctx, Map<String, Object> condition) {
		Player player = ctx.player();
		if (player == null || player.getAbilities().instabuild) return;

		// The same stacks the condition counted, so what gets taken is what was checked.
		int remaining = requiredCount(condition);
		for (ItemStack stack : stacksFor(ctx, player, condition)) {
			if (remaining <= 0) break;
			if (!matches(stack, condition)) continue;

			int taken = Math.min(remaining, stack.getCount());
			stack.shrink(taken);
			remaining -= taken;
		}
	}

	/** Tells the player why nothing happened, when the condition supplies a {@code message}. */
	private static void sendFailureMessage(InteractionContext ctx, Map<String, Object> condition) {
		String message = (String) condition.get("message");
		if (message == null || ctx.player() == null) return;

		if (Boolean.FALSE.equals(condition.get("overlay"))) {
			ctx.player().sendSystemMessage(Component.literal(message));
		} else {
			ctx.player().sendOverlayMessage(Component.literal(message));
		}
	}

	// -- shared parsing ----------------------------------------------------------------------------

	/**
	 * Whether a number falls within the condition's {@code min} and {@code max}, either of which may be
	 * omitted for an open end. An {@code equals} value asks for one exact number instead.
	 */
	private static boolean inRange(double value, Map<String, Object> condition) {
		Object exact = condition.get("equals");
		if (exact instanceof Number number) return value == number.doubleValue();

		Object min = condition.get("min");
		if (min instanceof Number number && value < number.doubleValue()) return false;

		Object max = condition.get("max");
		return !(max instanceof Number number) || !(value > number.doubleValue());
	}

	private static int requiredCount(Map<String, Object> condition) {
		return Math.max(((Number) condition.getOrDefault("count", 1)).intValue(), 1);
	}

	/**
	 * The stacks a {@code where} names — the held item unless it says otherwise. Besides {@code hand}
	 * and {@code inventory} it takes {@code armor} for the four worn pieces, or any single equipment
	 * slot by name: {@code mainhand}, {@code offhand}, {@code head}, {@code chest}, {@code legs},
	 * {@code feet}.
	 */
	private static List<ItemStack> stacksFor(InteractionContext ctx, Player player, Map<String, Object> condition) {
		String where = ((String) condition.getOrDefault("where", WHERE_HAND)).toLowerCase(Locale.ROOT);

		return switch (where) {
			case WHERE_HAND -> List.of(ctx.heldStack());
			case WHERE_INVENTORY -> {
				List<ItemStack> stacks = new ArrayList<>();
				for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
					stacks.add(player.getInventory().getItem(i));
				}
				yield stacks;
			}
			case WHERE_ARMOR -> {
				List<ItemStack> stacks = new ArrayList<>(ARMOR_SLOTS.length);
				for (EquipmentSlot slot : ARMOR_SLOTS) stacks.add(player.getItemBySlot(slot));
				yield stacks;
			}
			case "mainhand", "offhand", "head", "chest", "legs", "feet" ->
					List.of(player.getItemBySlot(EquipmentSlot.byName(where)));
			default -> {
				LOGGER.warn("Condition \"where\" must be \"{}\", \"{}\", \"{}\" or an equipment slot, "
						+ "but was: {}", WHERE_HAND, WHERE_INVENTORY, WHERE_ARMOR, where);
				yield List.of(ctx.heldStack());
			}
		};
	}

	/** Durability left as a percentage. An item that cannot break is always at 100. */
	private static double durabilityPercent(ItemStack stack) {
		if (stack.isEmpty() || !stack.isDamageableItem() || stack.getMaxDamage() <= 0) return 100.0;
		return 100.0 * (stack.getMaxDamage() - stack.getDamageValue()) / stack.getMaxDamage();
	}

	/**
	 * Whether an enchantment is present, and at what level. {@code min}/{@code max}/{@code equals} apply
	 * to the level, so leaving them out means "at any level". Honours {@code where} like
	 * {@code has_item}, so a condition can ask about a worn helmet rather than the held item.
	 */
	private static boolean enchantment(InteractionContext ctx, Map<String, Object> condition) {
		Player player = ctx.player();
		if (player == null) {
			LOGGER.warn("Condition \"enchantment\" needs a player, but this event does not have one.");
			return false;
		}

		String name = (String) condition.get("enchantment");
		Identifier id = name == null ? null : Identifier.tryParse(name);
		if (id == null) {
			LOGGER.warn("Condition \"enchantment\" needs a valid \"enchantment\" id, but was: {}", name);
			return false;
		}

		// Enchantments are data-driven, so they live in the level's registries rather than the built-in ones.
		Optional<Holder.Reference<Enchantment>> holder = ctx.level().registryAccess()
				.lookupOrThrow(Registries.ENCHANTMENT)
				.get(ResourceKey.create(Registries.ENCHANTMENT, id));
		if (holder.isEmpty()) {
			LOGGER.warn("Condition \"enchantment\" names an unknown enchantment: {}", id);
			return false;
		}

		for (ItemStack stack : stacksFor(ctx, player, condition)) {
			int level = EnchantmentHelper.getItemEnchantmentLevel(holder.get(), stack);
			if (level > 0 && inRange(level, condition)) return true;
		}
		return false;
	}

	/** Reads a condition value that accepts either one id or a list of them. */
	private static List<String> ids(Map<String, Object> condition, String singular, String plural) {
		Object single = condition.get(singular);
		if (single instanceof String id) return List.of(id);

		Object many = condition.get(plural);
		if (many instanceof List<?> list) {
			List<String> ids = new ArrayList<>(list.size());
			for (Object entry : list) {
				if (entry instanceof String id) ids.add(id);
			}
			return ids;
		}

		LOGGER.warn("Condition is missing a \"{}\" or \"{}\" value: {}", singular, plural, condition);
		return List.of();
	}

	/** Reads a {@code conditions} value, or null when it is missing or the wrong shape. */
	private static @Nullable List<Map<String, Object>> asConditionList(Object raw) {
		if (!(raw instanceof List<?> list)) {
			LOGGER.warn("\"conditions\" must be a list of condition objects, but was: {}", raw);
			return null;
		}

		List<Map<String, Object>> conditions = new ArrayList<>(list.size());
		for (Object entry : list) {
			if (!(entry instanceof Map<?, ?> map)) {
				LOGGER.warn("Each entry of \"conditions\" must be an object, but was: {}", entry);
				return null;
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> condition = (Map<String, Object>) map;
			conditions.add(condition);
		}
		return conditions;
	}
}
