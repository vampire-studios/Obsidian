package io.github.vampirestudios.obsidian.api.obsidian.menu;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * A chest-style menu, and optionally the loot pool that fills it — the crate format.
 *
 * <p>Lives outside the item package because a crate is not an item: the same config is read from an
 * item's {@code menu_config}, from a block's, and from an {@code open_crate} action.
 */
public class CustomMenuConfig {
	public String title = "Custom Menu";
	public int rows = 3;

	@SerializedName("loot_pool")
	public LootPool lootPool;

	public boolean hasLoot() {
		return lootPool != null && lootPool.entries != null && !lootPool.entries.isEmpty();
	}

	public String getTitle() {
		return title != null ? title : "";
	}

	/**
	 * Rows clamped to what a chest-style menu can show, so a bad value in a pack opens a smaller menu
	 * instead of throwing while the player is holding the item.
	 */
	public int getRows() {
		return Math.clamp(rows, 1, 9);
	}

	/**
	 * A weighted pool of rewards rolled when the item is used. Used to build "crate" items: the roll happens
	 * once, on use, and the results are either handed straight to the player or shown in a read-only menu
	 * that hands them over when it closes.
	 */
	public static class LootPool {
		public Mode mode = Mode.PREVIEW;
		public int rolls = 1;
		/** When true a roll cannot pick an entry an earlier roll already picked. */
		public boolean unique = true;
		/** When true the used item is consumed, unless the player is in creative. */
		public boolean consume = true;
		public List<Entry> entries = new ArrayList<>();

		public Mode getMode() {
			return mode != null ? mode : Mode.PREVIEW;
		}

		/**
		 * Rolls the pool. Entries without a usable item or with a non-positive weight are ignored, and an
		 * entry whose {@code chance} roll fails simply produces nothing for that roll.
		 */
		public List<ItemStack> roll(RandomSource random) {
			List<ItemStack> results = new ArrayList<>();
			List<Entry> available = new ArrayList<>();
			for (Entry entry : entries) {
				if (entry != null && entry.item != null && !entry.item.isEmpty() && entry.weight > 0) available.add(entry);
			}

			for (int i = 0; i < rolls && !available.isEmpty(); i++) {
				Entry picked = pick(available, random);
				if (picked == null) break;
				if (unique) available.remove(picked);
				if (picked.chance < 1.0F && random.nextFloat() >= picked.chance) continue;
				results.add(picked.createStack(random));
			}
			return results;
		}

		private static Entry pick(List<Entry> available, RandomSource random) {
			int total = 0;
			for (Entry entry : available) total += entry.weight;
			if (total <= 0) return null;

			int target = random.nextInt(total);
			for (Entry entry : available) {
				target -= entry.weight;
				if (target < 0) return entry;
			}
			return null;
		}

		public enum Mode {
			/** Show the rolled rewards in a read-only menu, and hand them over when it closes. */
			PREVIEW,
			/** Put the rolled rewards straight into the player's inventory, with no menu. */
			INSTANT
		}
	}

	public static class Entry {
		public ItemStack item = ItemStack.EMPTY;
		public int weight = 1;
		public float chance = 1.0F;

		@SerializedName("min_count")
		public Integer minCount;
		@SerializedName("max_count")
		public Integer maxCount;

		/**
		 * Copies the entry's stack, applying the random count range if one was given. Without a range the
		 * stack's own count is kept, so the plain {@code "minecraft:diamond"} string form still works.
		 */
		public ItemStack createStack(RandomSource random) {
			ItemStack stack = item.copy();
			if (minCount == null && maxCount == null) return stack;

			int min = Math.max(minCount != null ? minCount : 1, 1);
			int max = Math.max(maxCount != null ? maxCount : min, min);
			int count = min == max ? min : min + random.nextInt(max - min + 1);
			stack.setCount(Math.min(count, stack.getMaxStackSize()));
			return stack;
		}
	}
}
