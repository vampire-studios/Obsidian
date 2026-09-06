package io.github.vampirestudios.obsidian.api.obsidian.world;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A shape of blocks that does something once it is built.
 *
 * <p>Deliberately not called a ritual: the same matching drives a summoning altar, a machine's
 * multiblock, a lock built out of the right blocks, or a shape that simply has to be completed before a
 * door opens. What the pattern *does* is an ordinary {@linkplain #events event list}, so the format only
 * describes the shape, what it costs, and what happens to it afterwards.
 */
public class Pattern {

	public transient Identifier id;

	/**
	 * The shape, bottom layer first.
	 *
	 * <p>Each layer is a list of rows running north to south, and each character in a row is one block
	 * running west to east. A space is "anything", which is how a pattern leaves room for the player to
	 * stand or for the shape to be non-rectangular.
	 */
	public List<List<String>> pattern = new ArrayList<>();

	/**
	 * What each character means: a block id, or a {@code #}-prefixed block tag. The characters
	 * {@code ' '} (anything) and {@code '_'} (must be air) are built in and need no key.
	 */
	public Map<String, String> keys = new HashMap<>();

	/**
	 * The character the player must click to activate the shape. Without one, clicking any block the
	 * pattern names will do.
	 */
	public String anchor;

	/** The item that activates the shape. Without one, any empty-handed click on it will. */
	public Identifier activator;

	/** Whether the activating item is consumed. */
	@SerializedName("consume_activator")
	public boolean consumeActivator = false;

	/** Whether the shape's blocks are removed when it activates. */
	@SerializedName("consume_pattern")
	public boolean consumePattern = false;

	/** What the shape's blocks become instead of being removed. Ignored unless {@code consume_pattern}. */
	@SerializedName("replace_with")
	public Identifier replaceWith;

	/** Whether the shape matches turned to any of the four horizontal facings. */
	public boolean rotate = true;

	/** What activating costs, beyond the activator itself. */
	public Cost cost;

	/**
	 * Action lists keyed by event name, in the shared event format. {@code on_activate} runs when the
	 * shape is completed and paid for; {@code on_fail} runs when it matched but could not be paid for.
	 */
	public Map<String, List<Map<String, Object>>> events = new HashMap<>();

	public List<Map<String, Object>> getEventActions(String event) {
		return events == null ? List.of() : events.getOrDefault(event, List.of());
	}

	/** Whether the shape declares anything at all to match. */
	public boolean isEmpty() {
		return pattern == null || pattern.isEmpty();
	}

	public static class Cost {
		/** Items taken from the activating player's inventory. All of them, or the activation fails. */
		public List<Entry> items = new ArrayList<>();

		/** Experience levels taken from the activating player. */
		@SerializedName("experience_levels")
		public int experienceLevels = 0;

		/** Whether the player must be able to pay in creative too. */
		@SerializedName("charge_creative")
		public boolean chargeCreative = false;

		public static class Entry {
			@SerializedName(value = "item", alternate = {"id"})
			public Identifier item;
			public int count = 1;

			public int count() {
				return Math.max(1, count);
			}
		}
	}
}
