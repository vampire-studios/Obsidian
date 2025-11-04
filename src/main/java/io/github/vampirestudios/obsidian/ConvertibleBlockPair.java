package io.github.vampirestudios.obsidian;

import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/**
 * This class is used to hold the link between two different blocks (See for example {@link OxidizableBlocksRegistry#registerOxidizableBlockPair(Block, Block)} and
 * {@link OxidizableBlocksRegistry#registerWaxableBlockPair(Block, Block)}).
 **/
public class ConvertibleBlockPair {
	private final Block original;
	private final Block converted;
	private final ConversionItem conversionItem;
	private final ConversionItem reversingItem;
	private SoundEvent sound;
	private Item droppedItem;

	/**
	 * @param original        - The original block which will be converted
	 * @param converted       - The block that the original one will be converted to
	 * @param conversionItems - The item that is used to convert the original block into the converted block
	 **/
	public ConvertibleBlockPair(Block original, Block converted, ConversionItem conversionItems) {
		this(original, converted, conversionItems, null, null, null);
	}

	/**
	 * @param original        - The original block which will be converted
	 * @param converted       - The block that the original one will be converted to
	 * @param conversionItems - The item that is used to convert the original block into the converted block
	 * @param sound           - The item that is used to reverse the converted block into the original block
	 **/
	public ConvertibleBlockPair(Block original, Block converted, ConversionItem conversionItems, SoundEvent sound) {
		this(original, converted, conversionItems, null, sound);
	}

	/**
	 * @param original        - The original block which will be converted
	 * @param converted       - The block that the original one will be converted to
	 * @param conversionItems - The item that is used to convert the original block into the converted block
	 * @param droppedItem     - The item that is dropped when converting the block
	 **/
	public ConvertibleBlockPair(Block original, Block converted, ConversionItem conversionItems, Item droppedItem) {
		this(original, converted, conversionItems, null, null, droppedItem);
	}

	/**
	 * @param original        - The original block which will be converted
	 * @param converted       - The block that the original one will be converted to
	 * @param conversionItems - The item that is used to convert the original block into the converted block
	 * @param sound           - The item that is used to reverse the converted block into the original block
	 * @param droppedItem     - The item that is dropped when converting the block
	 **/
	public ConvertibleBlockPair(Block original, Block converted, ConversionItem conversionItems, SoundEvent sound, Item droppedItem) {
		this(original, converted, conversionItems, null, sound, droppedItem);
	}

	/**
	 * @param original       - The original block which will be converted
	 * @param converted      - The block that the original one will be converted to
	 * @param conversionItem - The item that is used to convert the original block into the converted block
	 * @param reversingItem  - The item that is used to reverse the converted block into the original block
	 **/
	public ConvertibleBlockPair(Block original, Block converted, ConversionItem conversionItem, ConversionItem reversingItem) {
		this(original, converted, conversionItem, reversingItem, null, null);
	}

	/**
	 * @param original       - The original block which will be converted
	 * @param converted      - The block that the original one will be converted to
	 * @param conversionItem - The item that is used to convert the original block into the converted block
	 * @param reversingItem  - The item that is used to reverse the converted block into the original block
	 * @param sound          - The item that is used to reverse the converted block into the original block
	 **/
	public ConvertibleBlockPair(Block original, Block converted, ConversionItem conversionItem, ConversionItem reversingItem, SoundEvent sound) {
		this(original, converted, conversionItem, reversingItem, sound, null);
	}

	/**
	 * @param original       - The original block which will be converted
	 * @param converted      - The block that the original one will be converted to
	 * @param conversionItem - The item that is used to convert the original block into the converted block
	 * @param reversingItem  - The item that is used to reverse the converted block into the original block
	 * @param droppedItem    - The item that is dropped when converting the block
	 **/
	public ConvertibleBlockPair(Block original, Block converted, ConversionItem conversionItem, ConversionItem reversingItem, Item droppedItem) {
		this(original, converted, conversionItem, reversingItem, null, droppedItem);
	}

	/**
	 * @param original       - The original block which will be converted
	 * @param converted      - The block that the original one will be converted to
	 * @param conversionItem - The item that is used to convert the original block into the converted block
	 * @param reversingItem  - The item that is used to reverse the converted block into the original block
	 * @param sound          - The sound that will be played when converting the blocks
	 * @param droppedItem    - The item that will be dropped when converting the original block into the converted block
	 **/
	public ConvertibleBlockPair(Block original, Block converted, ConversionItem conversionItem, ConversionItem reversingItem, SoundEvent sound, Item droppedItem) {
		this.original = original;
		this.converted = converted;
		this.conversionItem = conversionItem;
		this.reversingItem = reversingItem;
		this.sound = sound;
		this.droppedItem = droppedItem;
	}

	public SoundEvent getSound() {
		return this.sound;
	}

	public void setSound(SoundEvent sound) {
		this.sound = sound;
	}

	public Block getOriginal() {
		return this.original;
	}

	public Block getConverted() {
		return this.converted;
	}

	public ConversionItem getConversionItem() {
		return this.conversionItem;
	}

	public ConversionItem getReversingItem() {
		return this.reversingItem;
	}

	public Item getDroppedItem() {
		return this.droppedItem;
	}

	public void setDroppedItem(Item droppedItem) {
		this.droppedItem = droppedItem;
	}

	public record ConversionItem(TagKey<Item> tag, Item item) {
		public ConversionItem {
			if ((tag == null) == (item == null)) {
				throw new IllegalArgumentException("Only one of the fields must be non-null");
			}
		}

		public static ConversionItem of(TagKey<Item> tag) {
			return new ConversionItem(tag, null);
		}

		public static ConversionItem of(Item item) {
			return new ConversionItem(null, item);
		}

		// Call this by parsing the stack in hand
		public boolean matches(ItemStack stack) {
			if (this.tag != null) return stack.is(this.tag);
			else {
				return stack.is(this.item);
			}
		}
	}
}
