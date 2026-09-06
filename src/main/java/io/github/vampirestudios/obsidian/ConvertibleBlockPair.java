package io.github.vampirestudios.obsidian;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Represents a conversion relationship between two blocks.
 *
 * <p>This can be used for block conversions such as oxidation, waxing,
 * stripping logs, scraping copper, or any custom conversion defined by a mod.</p>
 */
public final class ConvertibleBlockPair {
	private final Block original;
	private final Block converted;
	private final ConversionItem conversionItem;
	private final @Nullable ConversionItem reversingItem;
	private final @Nullable SoundEvent sound;
	private final @Nullable Item droppedItem;

	private ConvertibleBlockPair(Builder builder) {
		this.original = Objects.requireNonNull(builder.original, "original");
		this.converted = Objects.requireNonNull(builder.converted, "converted");
		this.conversionItem = Objects.requireNonNull(builder.conversionItem, "conversionItem");
		this.reversingItem = builder.reversingItem;
		this.sound = builder.sound;
		this.droppedItem = builder.droppedItem;
	}

	/**
	 * Creates a new builder for a convertible block pair.
	 *
	 * @param original the original block before conversion
	 * @param converted the resulting block after conversion
	 * @param conversionItem the item or item tag used to perform the conversion
	 * @return a new builder
	 */
	public static Builder builder(Block original, Block converted, ConversionItem conversionItem) {
		return new Builder(original, converted, conversionItem);
	}

	/**
	 * Returns the original block.
	 *
	 * @return the original block
	 */
	public Block original() {
		return this.original;
	}

	/**
	 * Returns the converted block.
	 *
	 * @return the converted block
	 */
	public Block converted() {
		return this.converted;
	}

	/**
	 * Returns the item or item tag used to convert the original block.
	 *
	 * @return the conversion item
	 */
	public ConversionItem conversionItem() {
		return this.conversionItem;
	}

	/**
	 * Returns the item or item tag used to reverse the conversion.
	 *
	 * @return the reverse conversion item, or {@code null} if the conversion cannot be reversed
	 */
	public @Nullable ConversionItem reversingItem() {
		return this.reversingItem;
	}

	/**
	 * Returns the sound played when the conversion occurs.
	 *
	 * @return the conversion sound, or {@code null} if no sound is played
	 */
	public @Nullable SoundEvent sound() {
		return this.sound;
	}

	/**
	 * Returns the item dropped when the conversion occurs.
	 *
	 * @return the dropped item, or {@code null} if no item is dropped
	 */
	public @Nullable Item droppedItem() {
		return this.droppedItem;
	}

	/**
	 * Builder for {@link ConvertibleBlockPair}.
	 */
	public static final class Builder {
		private final Block original;
		private final Block converted;
		private final ConversionItem conversionItem;

		private @Nullable ConversionItem reversingItem;
		private @Nullable SoundEvent sound;
		private @Nullable Item droppedItem;

		private Builder(Block original, Block converted, ConversionItem conversionItem) {
			this.original = original;
			this.converted = converted;
			this.conversionItem = conversionItem;
		}

		/**
		 * Sets the item or item tag used to reverse the conversion.
		 *
		 * @param reversingItem the reverse conversion item
		 * @return this builder
		 */
		public Builder reversingItem(ConversionItem reversingItem) {
			this.reversingItem = reversingItem;
			return this;
		}

		/**
		 * Sets the sound played when the conversion occurs.
		 *
		 * @param sound the conversion sound
		 * @return this builder
		 */
		public Builder sound(SoundEvent sound) {
			this.sound = sound;
			return this;
		}

		/**
		 * Sets the item dropped when the conversion occurs.
		 *
		 * @param droppedItem the dropped item
		 * @return this builder
		 */
		public Builder droppedItem(Item droppedItem) {
			this.droppedItem = droppedItem;
			return this;
		}

		/**
		 * Builds a new {@link ConvertibleBlockPair}.
		 *
		 * @return the built convertible block pair
		 */
		public ConvertibleBlockPair build() {
			return new ConvertibleBlockPair(this);
		}
	}

	/**
	 * Represents an item requirement for a block conversion.
	 *
	 * <p>A conversion item may either match a specific item or any item
	 * belonging to an item tag.</p>
	 */
	public sealed interface ConversionItem permits ItemConversionItem, TagConversionItem {

		/**
		 * Checks whether the given item stack satisfies this conversion requirement.
		 *
		 * @param stack the item stack to test
		 * @return {@code true} if the stack matches this conversion item
		 */
		boolean matches(ItemStack stack);

		/**
		 * Creates a conversion requirement that matches a specific item.
		 *
		 * @param item the required item
		 * @return a conversion item
		 */
		static ConversionItem of(Item item) {
			return new ItemConversionItem(item);
		}

		/**
		 * Creates a conversion requirement that matches any item in the given tag.
		 *
		 * @param tag the required item tag
		 * @return a conversion item
		 */
		static ConversionItem of(TagKey<Item> tag) {
			return new TagConversionItem(tag);
		}
	}

	/**
	 * A conversion requirement that matches a specific item.
	 *
	 * @param item the required item
	 */
	public record ItemConversionItem(Item item) implements ConversionItem {

		public ItemConversionItem {
			Objects.requireNonNull(item, "item");
		}

		@Override
		public boolean matches(ItemStack stack) {
			return stack.is(this.item);
		}
	}

	/**
	 * A conversion requirement that matches any item in a tag.
	 *
	 * @param tag the required item tag
	 */
	public record TagConversionItem(TagKey<Item> tag) implements ConversionItem {

		public TagConversionItem {
			Objects.requireNonNull(tag, "tag");
		}

		@Override
		public boolean matches(ItemStack stack) {
			return stack.is(this.tag);
		}
	}
}