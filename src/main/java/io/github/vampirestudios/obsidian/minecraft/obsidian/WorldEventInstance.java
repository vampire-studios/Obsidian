package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.EventActionHandler;
import io.github.vampirestudios.obsidian.api.InteractionContext;
import io.github.vampirestudios.obsidian.api.obsidian.world.WorldEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** One running {@link WorldEvent}: where it is, how far through it is, and the bar showing that. */
public class WorldEventInstance {

	private final WorldEvent definition;
	private final ServerLevel level;
	private final BlockPos origin;
	private final ServerBossEvent bar;

	private int stageIndex = -1;

	/** How many times the current stage has been run, for {@code repeat}. */
	private int runsOfStage;
	private int ticksInStage;
	private int ticksTotal;
	private int killsInStage;
	private boolean finished;

	/**
	 * The mobs the current stage's wave spawned, held directly rather than by id: the level has no
	 * lookup by UUID, and a reference tells us about despawns and {@code /kill} as readily as deaths.
	 */
	private final List<Entity> waveMobs = new ArrayList<>();
	private int waveSize;

	/** Total health the wave was spawned with, for a bar that follows health rather than headcount. */
	private float waveHealth;

	/** Mobs still waiting to arrive, when the wave trickles in. */
	private final List<WorldEvent.Wave.Spawn> pending = new ArrayList<>();
	private int ticksSinceSpawn;

	/**
	 * Everyone who has been in range while this ran, whether or not they are still here.
	 *
	 * <p>Held by id rather than by reference so a player who logs out and back in is still the same
	 * participant, and so a finished event holds nothing alive.
	 */
	private final Set<UUID> participants = new LinkedHashSet<>();

	/** Kills of this event's own mobs, per participant. Everything else is participation, not contribution. */
	private final Map<UUID, Integer> contribution = new HashMap<>();

	/** Mobs the wave marked {@code guard}: protected rather than fought, and not part of clearing it. */
	private final List<Entity> guards = new ArrayList<>();

	/** Whether any guard has ever been spawned, so an event with none is not instantly lost. */
	private boolean guardsSpawned;

	/** The block the event started on, so {@code fail_when.block_broken} can tell it has gone. */
	private final BlockState originBlock;

	public WorldEventInstance(WorldEvent definition, ServerLevel level, BlockPos origin) {
		this.definition = definition;
		this.level = level;
		this.origin = origin;
		this.originBlock = level.getBlockState(origin);

		this.bar = new ServerBossEvent(UUID.randomUUID(), title(null),
				definition.bar.color(), definition.bar.overlay());
		applyBar(definition.bar);
	}

	/** Puts a bar's look on the event's bar, for the event's own and for a stage that overrides it. */
	private void applyBar(WorldEvent.Bar look) {
		bar.setColor(look.color());
		bar.setOverlay(look.overlay());
		bar.setDarkenScreen(look.darkenSky);
		bar.setPlayBossMusic(look.bossMusic);
		bar.setCreateWorldFog(look.fog);
	}

	public WorldEvent definition() {
		return definition;
	}

	public ServerLevel level() {
		return level;
	}

	public BlockPos origin() {
		return origin;
	}

	public boolean finished() {
		return finished;
	}

	/** Runs {@code on_start} and enters the first stage. */
	public void start() {
		run(definition.getEventActions("on_start"));
		enterStage(0);
	}

	/**
	 * One tick of the event.
	 *
	 * @return whether the event is still running
	 */
	public boolean tick() {
		if (finished) return false;

		updateViewers();
		if (definition.cancelWhenEmpty && bar.getPlayers().isEmpty()) {
			cancel();
			return false;
		}

		ticksInStage++;
		ticksTotal++;
		pruneWave();

		WorldEvent.Stage stage = currentStage();
		releasePending(stage);

		if (definition.timeLimit > 0 && ticksTotal >= definition.timeLimit) {
			fail();
			return false;
		}

		if (hasLost()) {
			fail();
			return false;
		}

		// A trickling wave is not cleared just because the first mob died and the rest have not arrived.
		if (stage != null && stage.advanceWhen != null && !stage.advanceWhen.isManual() && pending.isEmpty()) {
			if (progressOfStage(stage) >= 1.0F) advance();
		}
		bar.setProgress(progress());
		return !finished;
	}

	/** Counts a death towards the current stage, when it is the kind the stage is waiting for. */
	public void onDeath(LivingEntity dead, @Nullable Entity killer) {
		if (finished) return;

		creditKill(dead, killer);

		WorldEvent.Stage stage = currentStage();
		if (stage == null || stage.advanceWhen == null || stage.advanceWhen.kills == null) return;
		if (stage.advanceWhen.kills.entity == null) return;

		if (!stage.advanceWhen.kills.entity.equals(BuiltInRegistries.ENTITY_TYPE.getKey(dead.getType()))) return;
		if (dead.level() != level) return;
		if (dead.blockPosition().distSqr(origin) > (long) definition.radius * definition.radius) return;

		killsInStage++;
	}

	/** Ends the current stage and begins the next, completing the event after the last one. */
	public void advance() {
		WorldEvent.Stage stage = currentStage();
		if (stage != null) run(stage.onExit);

		// A repeating stage runs again rather than moving on: the same wave, freshly spawned.
		if (stage != null && (stage.endless() || runsOfStage < stage.repeat)) {
			runsOfStage++;
			restartStage();
			return;
		}

		if (stageIndex + 1 >= definition.stages.size()) {
			complete();
			return;
		}
		enterStage(stageIndex + 1);
	}

	public void complete() {
		if (finished) return;
		finished = true;
		run(definition.getEventActions("on_complete"));
		giveRewards();
		close();
	}

	/**
	 * Hands out the reward tiers. Only on completion — losing a fight pays nothing beyond whatever
	 * {@code on_fail} chooses to give.
	 */
	private void giveRewards() {
		WorldEvent.Rewards rewards = definition.rewards;
		if (rewards == null) return;

		for (ServerPlayer player : onlineParticipants()) {
			runFor(player, rewards.participation);

			if (contribution.getOrDefault(player.getUUID(), 0) >= rewards.threshold()) {
				runFor(player, rewards.contribution);
			}
		}

		ServerPlayer best = topContributor();
		if (best != null) runFor(best, rewards.top);
	}

	/** The participant with the most kills, or null when nobody landed one. */
	private @Nullable ServerPlayer topContributor() {
		UUID best = null;
		int most = 0;

		for (Map.Entry<UUID, Integer> entry : contribution.entrySet()) {
			if (entry.getValue() <= most) continue;
			most = entry.getValue();
			best = entry.getKey();
		}
		return best == null ? null : level.getServer().getPlayerList().getPlayer(best);
	}

	/** Runs an action list for one player. World actions in a reward list still run at the origin. */
	private void runFor(ServerPlayer player, List<Map<String, Object>> actions) {
		if (actions == null || actions.isEmpty()) return;

		InteractionContext ctx = InteractionContext.ofBlock(level, origin, player, null);
		for (Map<String, Object> actionConfig : actions) {
			EventActionHandler.dispatch(ctx, (String) actionConfig.get("action"), actionConfig, "world_event");
		}
	}

	public void cancel() {
		if (finished) return;
		finished = true;
		run(definition.getEventActions("on_cancel"));
		close();
	}

	/**
	 * Credits a participant for killing one of this event's own mobs.
	 *
	 * <p>Only the killing blow counts, which is all the death hook can see. Someone healing, tanking or
	 * building barricades contributes nothing measurable here — which is exactly why the participation
	 * tier exists and why contribution is a bonus on top of it rather than the whole reward.
	 */
	private void creditKill(LivingEntity dead, @Nullable Entity killer) {
		if (!(killer instanceof Player player)) return;
		if (!waveMobs.contains(dead)) return;
		if (!participants.contains(player.getUUID())) return;

		contribution.merge(player.getUUID(), 1, Integer::sum);
	}

	/**
	 * Whether a {@code fail_when} condition has been met.
	 *
	 * <p>Guards are only checked once some have been spawned: a stage with none has not lost them.
	 */
	private boolean hasLost() {
		WorldEvent.FailWhen failWhen = definition.failWhen;
		if (failWhen == null) return false;

		if (failWhen.guardDies && guardsSpawned && guards.isEmpty()) return true;

		// Compared by block rather than by exact state, so a door opening or a block being waterlogged
		// is not "broken" while breaking or replacing it is.
		return failWhen.blockBroken && !level.getBlockState(origin).is(originBlock.getBlock());
	}

	/** Ran out of time, or lost what it was protecting. Distinct from {@link #cancel()}. */
	public void fail() {
		if (finished) return;
		finished = true;
		run(definition.getEventActions("on_fail"));
		close();
	}

	private void enterStage(int index) {
		stageIndex = index;
		runsOfStage = 1;
		restartStage();
	}

	/** Begins the current stage, or begins it again for a repeat. */
	private void restartStage() {
		ticksInStage = 0;
		killsInStage = 0;

		if (stageIndex >= definition.stages.size()) {
			complete();
			return;
		}

		WorldEvent.Stage stage = definition.stages.get(stageIndex);
		bar.setName(title(stage));
		bar.setProgress(0.0F);
		applyBar(stage.bar != null ? stage.bar : definition.bar);
		run(stage.onEnter);
		spawnWave(stage.wave);
	}

	private WorldEvent.Stage currentStage() {
		if (stageIndex < 0 || stageIndex >= definition.stages.size()) return null;
		return definition.stages.get(stageIndex);
	}

	/** How far through the current stage the event is, 0–1. A manual stage sits at zero. */
	private float progress() {
		WorldEvent.Stage stage = currentStage();
		return stage == null ? 1.0F : progressOfStage(stage);
	}

	private float progressOfStage(WorldEvent.Stage stage) {
		WorldEvent.Advance advance = stage.advanceWhen;
		if (advance == null || advance.isManual()) return 0.0F;

		if (advance.waveCleared) {
			// A wave that spawned nothing is already cleared, or the stage would never end.
			if (waveSize <= 0) return 1.0F;

			if (stage.wave != null && stage.wave.trackHealth && waveHealth > 0.0F) {
				return Math.clamp(1.0F - remainingHealth() / waveHealth, 0.0F, 1.0F);
			}
			return Math.clamp((float) (waveSize - waveMobs.size()) / waveSize, 0.0F, 1.0F);
		}

		if (advance.kills != null && advance.kills.entity != null) {
			return Math.clamp((float) killsInStage / advance.kills.count(), 0.0F, 1.0F);
		}
		return Math.clamp((float) ticksInStage / Math.max(1, advance.duration), 0.0F, 1.0F);
	}

	private Component title(WorldEvent.Stage stage) {
		if (stage != null && stage.name != null) return stage.name.getName();
		if (definition.name != null) return definition.name.getName();
		return Component.literal(definition.id == null ? "" : definition.id.getPath());
	}

	/** Adds players who have come into range and drops those who have left. */
	private void updateViewers() {
		double radiusSq = (double) definition.radius * definition.radius;
		Vec3 centre = Vec3.atCenterOf(origin);

		// Walked over a copy: removing from the bar while iterating its own view would fail, and a
		// player who logged out or changed dimension is no longer in level.players() to be caught below.
		for (ServerPlayer viewer : List.copyOf(bar.getPlayers())) {
			if (viewer.level() != level || viewer.position().distanceToSqr(centre) > radiusSq) {
				bar.removePlayer(viewer);
			}
		}

		for (ServerPlayer player : level.players()) {
			if (player.position().distanceToSqr(centre) > radiusSq) continue;

			bar.addPlayer(player);
			// Once a participant, always a participant: someone who fought the first wave and died is
			// still owed the reward at the end.
			participants.add(player.getUUID());
		}
	}

	/**
	 * Spawns a wave, scattered around the origin and scaled for how many players are taking part.
	 */
	private void spawnWave(WorldEvent.@Nullable Wave wave) {
		waveMobs.clear();
		pending.clear();
		waveSize = 0;
		waveHealth = 0.0F;
		ticksSinceSpawn = 0;
		if (wave == null || wave.spawns.isEmpty()) return;

		int players = Math.max(1, bar.getPlayers().size());

		// Built as a list first: the wave's size is known up front even when it arrives over time, so
		// the bar has something to measure against from the first tick.
		for (WorldEvent.Wave.Spawn spawn : wave.spawns) {
			if (spawn.entity == null) continue;

			if (BuiltInRegistries.ENTITY_TYPE.getValue(spawn.entity) == null) {
				Obsidian.LOGGER.warn("World event {} spawns unknown entity {}.", definition.id, spawn.entity);
				continue;
			}

			// Guards arrive at once and are never part of the wave's size: the stage is not waiting for
			// them to die, and scaling the villager you are protecting with the party size is nonsense.
			// They are spawned once for the whole event, so a repeating stage does not stack up villagers.
			if (spawn.guard) {
				if (guardsSpawned) continue;

				for (int i = 0; i < spawn.count(); i++) {
					Entity guard = spawnOne(spawn, wave);
					if (guard != null) guards.add(guard);
				}
				guardsSpawned = !guards.isEmpty();
				continue;
			}

			int count = wave.scaledCount(spawn.count(), players);
			for (int i = 0; i < count; i++) pending.add(spawn);
		}
		waveSize = pending.size();

		if (wave.intervalTicks() == 0) {
			// Spawned in one go: releasePending would otherwise take a tick each.
			while (!pending.isEmpty()) release(wave);
		}
	}

	/** Lets the next mob of a trickling wave arrive, when it is due. */
	private void releasePending(WorldEvent.@Nullable Stage stage) {
		if (pending.isEmpty() || stage == null || stage.wave == null) return;

		int interval = stage.wave.intervalTicks();
		if (interval == 0) return;

		if (++ticksSinceSpawn < interval) return;
		ticksSinceSpawn = 0;
		release(stage.wave);
	}

	private void release(WorldEvent.Wave wave) {
		WorldEvent.Wave.Spawn spawn = pending.removeFirst();

		Entity mob = spawnOne(spawn, wave);
		if (mob == null) {
			// Nowhere to put it. It still counted towards the wave's size, so drop that too or the
			// stage could never finish.
			waveSize = Math.max(0, waveSize - 1);
			return;
		}

		waveMobs.add(mob);
		if (mob instanceof LivingEntity living) waveHealth += living.getMaxHealth();
	}

	/** The health left across the wave, for a bar that follows it. */
	private float remainingHealth() {
		float total = 0.0F;
		for (Entity mob : waveMobs) {
			if (mob instanceof LivingEntity living) total += Math.max(0.0F, living.getHealth());
		}
		return total;
	}

	private @Nullable Entity spawnOne(WorldEvent.Wave.Spawn spawn, WorldEvent.Wave wave) {
		for (int attempt = 0; attempt < wave.attempts(); attempt++) {
			BlockPos at = scatter(wave.spreadBlocks());
			if (!isFree(at)) continue;

			Entity mob = MobSpawner.spawn(level, at, spawn);
			if (mob != null) return mob;
		}
		return null;
	}

	/** A ground position within {@code spread} blocks of the origin. */
	private BlockPos scatter(int spread) {
		if (spread <= 0) return origin;

		int x = origin.getX() + level.getRandom().nextInt(spread * 2 + 1) - spread;
		int z = origin.getZ() + level.getRandom().nextInt(spread * 2 + 1) - spread;
		int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
		return new BlockPos(x, y, z);
	}

	/** Two blocks of clear space, which is enough for the mobs a wave is usually made of. */
	private boolean isFree(BlockPos at) {
		return level.getBlockState(at).isAir() && level.getBlockState(at.above()).isAir();
	}

	/** Drops mobs that have died, despawned or been removed some other way. */
	private void pruneWave() {
		waveMobs.removeIf(mob -> mob.isRemoved() || !mob.isAlive());
		guards.removeIf(guard -> guard.isRemoved() || !guard.isAlive());
	}

	private void close() {
		WorldEvent.Stage stage = currentStage();
		if (stage != null && stage.wave != null && stage.wave.despawnOnEnd) {
			waveMobs.forEach(Entity::discard);
		}
		waveMobs.clear();

		bar.removeAllPlayers();
		bar.setVisible(false);
	}

	/**
	 * Runs an action list.
	 *
	 * <p>Actions that act on the world run once, at the event's origin. Actions that act on a player run
	 * once **per participant** — which is what makes {@code send_message} an announcement and
	 * {@code give_item} a reward, rather than something with nobody to apply it to.
	 */
	private void run(List<Map<String, Object>> actions) {
		if (actions == null || actions.isEmpty()) return;

		InteractionContext worldCtx = InteractionContext.ofBlock(level, origin, null, null);

		for (Map<String, Object> actionConfig : actions) {
			String action = (String) actionConfig.get("action");

			if (!EventActionHandler.needsPlayer(action)) {
				EventActionHandler.dispatch(worldCtx, action, actionConfig, "world_event");
				continue;
			}

			for (ServerPlayer player : onlineParticipants()) {
				EventActionHandler.dispatch(InteractionContext.ofBlock(level, origin, player, null),
						action, actionConfig, "world_event");
			}
		}
	}

	/** The participants who are still connected. Someone who logged out simply misses out. */
	private List<ServerPlayer> onlineParticipants() {
		List<ServerPlayer> online = new ArrayList<>();
		for (UUID id : participants) {
			ServerPlayer player = level.getServer().getPlayerList().getPlayer(id);
			if (player != null) online.add(player);
		}
		return online;
	}
}
