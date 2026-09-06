package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.world.WorldEvent;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.jspecify.annotations.Nullable;

import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The running {@link WorldEvent}s.
 *
 * <p>Events live only as long as the server does — nothing is written to the save. A fight that is
 * interrupted by a restart is simply over, which is the behaviour a pack wants far more often than a
 * half-finished siege resuming into an empty world.
 */
public final class WorldEventManager {

	private static final List<WorldEventInstance> RUNNING = new ArrayList<>();

	/** When each event last ended, per dimension, for {@code cooldown}. Cleared with the server. */
	private static final Map<String, Long> ENDED = new HashMap<>();

	/**
	 * Ticks since the server started, counted here rather than read from the level: cooldowns want
	 * elapsed real ticks, and a level's clock can be set, slowed or frozen by a pack or a command.
	 */
	private static long ticks;

	private WorldEventManager() {
	}

	/**
	 * Starts an event at a position. Several copies of the same event can run at once, in different
	 * places — a siege at two villages is two instances.
	 *
	 * @return the instance, or null when the event is not registered
	 */
	public static @Nullable WorldEventInstance start(ServerLevel level, BlockPos origin, Identifier id) {
		WorldEvent definition = ContentRegistries.WORLD_EVENTS.getValue(id);
		if (definition == null) {
			Obsidian.LOGGER.warn("Tried to start unknown world event {}.", id);
			return null;
		}

		if (definition.unique && isRunning(level, id)) {
			Obsidian.LOGGER.debug("World event {} is unique and already running in {}.", id, level.dimension().identifier());
			return null;
		}

		if (onCooldown(level, id, definition)) {
			Obsidian.LOGGER.debug("World event {} is still on cooldown.", id);
			return null;
		}

		WorldEventInstance instance = new WorldEventInstance(definition, level, origin);
		RUNNING.add(instance);
		instance.start();
		return instance;
	}

	/** Advances the nearest running copy of an event, for a manual stage. */
	public static boolean advance(ServerLevel level, BlockPos near, Identifier id) {
		WorldEventInstance instance = nearest(level, near, id);
		if (instance == null) return false;

		instance.advance();
		return true;
	}

	/** Cancels the nearest running copy of an event. */
	public static boolean stop(ServerLevel level, BlockPos near, Identifier id) {
		WorldEventInstance instance = nearest(level, near, id);
		if (instance == null) return false;

		instance.cancel();
		return true;
	}

	/** Ticks every running event and drops the ones that ended. */
	public static void tick() {
		ticks++;
		if (RUNNING.isEmpty()) return;

		Iterator<WorldEventInstance> iterator = RUNNING.iterator();
		while (iterator.hasNext()) {
			WorldEventInstance instance = iterator.next();
			if (instance.tick()) continue;

			noteEnded(instance);
			iterator.remove();
		}
	}

	/**
	 * Offers a death to every running event; each decides whether it was waiting for one, and whether
	 * the killer earns credit for it.
	 */
	public static void onDeath(LivingEntity dead, @Nullable Entity killer) {
		if (RUNNING.isEmpty()) return;
		// Copied: an event completing on this death removes itself from the list.
		for (WorldEventInstance instance : List.copyOf(RUNNING)) {
			instance.onDeath(dead, killer);
		}
	}

	/** Starts the cooldown for an event that has just ended. */
	private static void noteEnded(WorldEventInstance instance) {
		WorldEvent definition = instance.definition();
		if (definition.cooldown <= 0 || definition.id == null) return;

		ENDED.put(cooldownKey(instance.level(), definition.id), ticks);
	}

	private static boolean onCooldown(ServerLevel level, Identifier id, WorldEvent definition) {
		if (definition.cooldown <= 0) return false;

		Long ended = ENDED.get(cooldownKey(level, id));
		return ended != null && ticks - ended < definition.cooldown;
	}

	private static String cooldownKey(ServerLevel level, Identifier id) {
		return level.dimension().identifier() + "|" + id;
	}

	/** Drops everything, for a server stopping or a world unloading. */
	public static void clear() {
		RUNNING.forEach(WorldEventInstance::cancel);
		RUNNING.clear();
		ENDED.clear();
	}

	/** Whether a copy of this event is already running in that dimension. */
	public static boolean isRunning(ServerLevel level, Identifier id) {
		for (WorldEventInstance instance : RUNNING) {
			if (instance.finished() || instance.level() != level) continue;
			if (id.equals(instance.definition().id)) return true;
		}
		return false;
	}

	/** The closest running copy of an event in the same level, or null when none is running. */
	private static @Nullable WorldEventInstance nearest(ServerLevel level, BlockPos near, Identifier id) {
		WorldEventInstance best = null;
		double bestDistance = Double.MAX_VALUE;

		for (WorldEventInstance instance : RUNNING) {
			if (instance.finished()) continue;
			if (instance.level() != level) continue;
			if (!id.equals(instance.definition().id)) continue;

			double distance = instance.origin().distSqr(near);
			if (distance < bestDistance) {
				bestDistance = distance;
				best = instance;
			}
		}
		return best;
	}
}
