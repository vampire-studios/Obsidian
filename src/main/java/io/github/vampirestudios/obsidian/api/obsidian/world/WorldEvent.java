package io.github.vampirestudios.obsidian.api.obsidian.world;

import com.google.gson.annotations.SerializedName;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import net.minecraft.resources.Identifier;
import net.minecraft.world.BossEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A staged world event with a boss bar: a fight, a siege, a countdown, a ritual that takes a while.
 *
 * <p>An event does not start on its own. Something has to fire the {@code start_event} action —
 * a [pattern] being activated, a block being clicked, an item being used — which is what keeps the
 * format from needing triggers of its own.
 */
public class WorldEvent {

	public transient Identifier id;

	/** The bar's title. Each stage may override it. */
	public NameInformation name;

	/** How the bar looks. */
	public Bar bar = new Bar();

	/** How far from the event's origin a player has to be to see the bar and count towards it, in blocks. */
	public int radius = 64;

	/** The stages, run in order. An event with none starts and immediately completes. */
	public List<Stage> stages = new ArrayList<>();

	/**
	 * Action lists keyed by event name: {@code on_start} when it begins, {@code on_complete} when the
	 * last stage ends, {@code on_cancel} when it is stopped early or everyone leaves.
	 */
	public Map<String, List<Map<String, Object>>> events = new HashMap<>();

	public List<Map<String, Object>> getEventActions(String event) {
		return events == null ? List.of() : events.getOrDefault(event, List.of());
	}

	/** Whether the event ends when the last player leaves its radius. */
	@SerializedName("cancel_when_empty")
	public boolean cancelWhenEmpty = true;

	/**
	 * Ticks the whole event may run before it is lost. {@code 0} lets it run forever.
	 *
	 * <p>Running out fires {@code on_fail} rather than {@code on_cancel}: being too slow is a different
	 * outcome from walking away, and a pack usually wants to say so.
	 */
	@SerializedName("time_limit")
	public int timeLimit = 0;

	/**
	 * Whether only one copy of this event may run per dimension. A second {@code start_event} while one
	 * is running is ignored rather than stacking a second bar on the first.
	 */
	public boolean unique = false;

	/** Ticks after an event ends before the same one may be started again in that dimension. */
	public int cooldown = 0;

	/** What loses the event, beyond running out of {@link #timeLimit}. */
	@SerializedName("fail_when")
	public FailWhen failWhen;

	/** What the participants get for finishing it. */
	public Rewards rewards;

	/**
	 * What loses the event.
	 *
	 * <p>Without one, an event can only be won, timed out or walked away from — which leaves defending
	 * and escorting inexpressible, since there is nothing to fail at.
	 */
	public static class FailWhen {
		/** Whether losing every mob marked {@code guard} loses the event. */
		@SerializedName(value = "guard_dies", alternate = {"guards_die"})
		public boolean guardDies = false;

		/** Whether the block the event started on being broken or replaced loses it. */
		@SerializedName("block_broken")
		public boolean blockBroken = false;
	}

	/**
	 * Rewards, in tiers.
	 *
	 * <p>Everyone who turned up gets {@code participation}, whether or not they landed a hit — someone
	 * healing, building or drawing aggro contributed something the event cannot measure, and giving them
	 * nothing reads as a bug. {@code contribution} and {@code top} sit on top of that.
	 */
	public static class Rewards {
		/** Actions for every participant, however little they did. */
		public List<Map<String, Object>> participation = new ArrayList<>();

		/** Actions for participants who reached {@link #threshold}. */
		public List<Map<String, Object>> contribution = new ArrayList<>();

		/** Actions for the single highest contributor, when anyone contributed at all. */
		public List<Map<String, Object>> top = new ArrayList<>();

		/** Kills of the event's own mobs needed to count as a contributor. */
		public int threshold = 1;

		public int threshold() {
			return Math.max(1, threshold);
		}
	}

	public static class Bar {
		public String color = "white";
		public String overlay = "progress";

		@SerializedName("darken_sky")
		public boolean darkenSky = false;

		@SerializedName("boss_music")
		public boolean bossMusic = false;

		public boolean fog = false;

		public BossEvent.BossBarColor color() {
			return parse(BossEvent.BossBarColor.values(), color, BossEvent.BossBarColor.WHITE);
		}

		public BossEvent.BossBarOverlay overlay() {
			return parse(BossEvent.BossBarOverlay.values(), overlay, BossEvent.BossBarOverlay.PROGRESS);
		}

		private static <T extends Enum<T>> T parse(T[] values, String declared, T fallback) {
			if (declared == null || declared.isBlank()) return fallback;
			for (T value : values) {
				if (value.name().equalsIgnoreCase(declared.trim())) return value;
			}
			return fallback;
		}
	}

	public static class Stage {
		/** Overrides the event's name in the bar while this stage runs. */
		public NameInformation name;

		/** What ends the stage. Declaring none leaves it running until {@code advance_event}. */
		@SerializedName(value = "advance_when", alternate = {"until"})
		public Advance advanceWhen;

		/** Actions run as the stage begins. */
		@SerializedName("on_enter")
		public List<Map<String, Object>> onEnter = new ArrayList<>();

		/** Actions run as the stage ends, however it ended. */
		@SerializedName("on_exit")
		public List<Map<String, Object>> onExit = new ArrayList<>();

		/** Mobs spawned as the stage begins, and tracked until they are gone. */
		public Wave wave;

		/**
		 * How many times the stage runs before the event moves on. {@code 0} or less runs it forever,
		 * until something stops the event — which is how a thing that keeps leaking is expressed.
		 */
		public int repeat = 1;

		/** Overrides the event's bar while this stage runs — a final phase can turn it red. */
		public Bar bar;

		public boolean endless() {
			return repeat <= 0;
		}
	}

	/**
	 * A wave of mobs.
	 *
	 * <p>Spawned mobs are tracked individually, so {@code wave_cleared} means "the mobs this wave
	 * spawned are gone" rather than "some number of that kind died somewhere" — a wave is not advanced
	 * by a player killing naturally-spawned mobs of the same type nearby.
	 */
	public static class Wave {
		/** What the wave is made of. */
		public List<Spawn> spawns = new ArrayList<>();

		/** How far from the origin mobs are scattered, in blocks. */
		public int spread = 8;

		/** How many placements are tried per mob before giving up on it. */
		@SerializedName("spawn_attempts")
		public int spawnAttempts = 16;

		/**
		 * Extra mobs per player beyond the first, as a fraction. {@code 0.5} with three players in range
		 * spawns twice the declared count; {@code 0} ignores how many are there.
		 */
		@SerializedName("per_player")
		public float perPlayer = 0.0F;

		/** Whether mobs still alive when the event ends are removed with it. */
		@SerializedName("despawn_on_end")
		public boolean despawnOnEnd = true;

		/**
		 * Ticks between mobs arriving. {@code 0} spawns the whole wave at once; anything higher trickles
		 * them in, which reads very differently in play.
		 */
		public int interval = 0;

		/**
		 * Whether the bar follows the wave's remaining health rather than how many of it are left. This
		 * is what a bar for a single boss wants — a one-mob wave otherwise shows nothing until it dies.
		 */
		@SerializedName("track_health")
		public boolean trackHealth = false;

		public int intervalTicks() {
			return Math.max(0, interval);
		}

		public int spreadBlocks() {
			return Math.max(0, spread);
		}

		public int attempts() {
			return Math.max(1, spawnAttempts);
		}

		/** The declared count scaled for how many players are taking part. */
		public int scaledCount(int declared, int players) {
			if (perPlayer <= 0.0F || players <= 1) return declared;
			return Math.max(declared, Math.round(declared * (1.0F + perPlayer * (players - 1))));
		}

		/**
		 * One kind of mob in a wave, and what makes it more than the vanilla default.
		 *
		 * <p>The same shape is accepted by the {@code spawn_entity} action, so a mob described for a
		 * wave can be summoned from anywhere else without being described twice.
		 */
		public static class Spawn extends MobSpec {
			public int count = 1;

			/**
			 * Whether this mob is being protected rather than fought.
			 *
			 * <p>A guard is not part of what {@code wave_cleared} waits for — you do not kill the
			 * villager to finish the stage — and losing every guard fails the event when
			 * {@code fail_when.guard_dies} says so.
			 */
			public boolean guard = false;

			public int count() {
				return Math.max(1, count);
			}
		}
	}

	public static class Advance {
		/** Ticks the stage lasts. */
		public int duration = 0;

		/** Entities of a kind that must die within the radius before the stage ends. */
		public Kills kills;

		/** Whether the stage ends once the mobs its own wave spawned are gone. */
		@SerializedName(value = "wave_cleared", alternate = {"cleared"})
		public boolean waveCleared = false;

		public boolean isManual() {
			return duration <= 0 && !waveCleared && (kills == null || kills.entity == null);
		}

		public static class Kills {
			public Identifier entity;
			public int count = 1;

			public int count() {
				return Math.max(1, count);
			}
		}
	}

	/** Normalises a stage index for the log. */
	public String describeStage(int index) {
		return id + " stage " + (index + 1) + "/" + stages.size();
	}

	public String toString() {
		return id == null ? super.toString() : id.toString().toLowerCase(Locale.ROOT);
	}
}
