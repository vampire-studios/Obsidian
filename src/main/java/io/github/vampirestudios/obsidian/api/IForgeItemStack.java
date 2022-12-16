package io.github.vampirestudios.obsidian.api;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/*
 * Extension added to ItemStack that bounces to ItemSack sensitive Item methods. Typically this is just for convince.
 */
public interface IForgeItemStack {
	// Helpers for accessing Item data
	private ItemStack self() {
		return (ItemStack) this;
	}

	/**
	 * Checks whether an item can be enchanted with a certain enchantment. This
	 * applies specifically to enchanting an item in the enchanting table and is
	 * called when retrieving the list of possible enchantments for an item.
	 * Enchantments may additionally (or exclusively) be doing their own checks in
	 * {@link Enchantment#canApplyAtEnchantingTable(ItemStack)};
	 * check the individual implementation for reference. By default, this will check
	 * if the enchantment type is valid for this item type.
	 *
	 * @param enchantment the enchantment to be applied
	 * @return true if the enchantment can be applied to this item
	 */
	default boolean canApplyAtEnchantingTable(Enchantment enchantment) {
		return self().getItem().canApplyAtEnchantingTable(self(), enchantment);
	}

	/**
	 * Gets the level of the enchantment currently present on the stack. By default, returns the enchantment level present in NBT.
	 * <p>
	 * Equivalent to calling {@link EnchantmentHelper#getLevel(Enchantment, ItemStack)}
	 * Use in place of {@link EnchantmentHelper#getTagEnchantmentLevel(Enchantment, ItemStack)} for checking presence of an enchantment in logic implementing the enchantment behavior.
	 * Use {@link EnchantmentHelper#getTagEnchantmentLevel(Enchantment, ItemStack)} instead when modifying an item's enchantments.
	 *
	 * @param enchantment the enchantment being checked for
	 * @return Level of the enchantment, or 0 if not present
	 * @see EnchantmentHelper#getTagEnchantmentLevel(Enchantment, ItemStack)
	 */
	default int getEnchantmentLevel(Enchantment enchantment) {
		return self().getItem().getEnchantmentLevel(self(), enchantment);
	}

	/**
	 * ItemStack sensitive version of {@link Item#getEnchantability()}.
	 *
	 * @return the enchantment value of this ItemStack
	 */
	default int getEnchantmentValue() {
		return self().getItem().getEnchantmentValue(self());
	}

	/**
	 * Determines the amount of durability the mending enchantment
	 * will repair, on average, per point of experience.
	 */
	default float getXpRepairRatio() {
		return self().getItem().getXpRepairRatio(self());
	}

	/**
	 * Allow or forbid the specific book/item combination as an anvil enchant
	 *
	 * @param book The book
	 * @return if the enchantment is allowed
	 */
	default boolean isBookEnchantable(ItemStack book) {
		return self().getItem().isBookEnchantable(self(), book);
	}

	/**
	 * Allow the item one last chance to modify its name used for the tool highlight
	 * useful for adding something extra that can't be removed by a user in the
	 * displayed name, such as a mode of operation.
	 *
	 * @param displayName the name that will be displayed unless it is changed in
	 *                    this method.
	 */
	default Text getHighlightTip(Text displayName) {
		return self().getItem().getHighlightTip(self(), displayName);
	}

	/**
	 * Determines if the ItemStack is equal to the other item stack, including Item, Count, and NBT.
	 *
	 * @param other     The other stack
	 * @return true if equals
	 */
	default boolean equals(ItemStack other) {
		if (self().isEmpty())
			return other.isEmpty();
		else
			return !other.isEmpty() && self().getCount() == other.getCount() && self().getItem() == other.getItem() &&
					ItemStack.areEqual(self(), other);
	}

	/**
	 * Called by Piglins when checking to see if they will give an item or something in exchange for this item.
	 *
	 * @return True if this item can be used as "currency" by piglins
	 */
	default boolean isPiglinCurrency()
	{
		return self().getItem().isPiglinCurrency(self());
	}

	/**
	 * Called by Piglins to check if a given item prevents hostility on sight. If this is true the Piglins will be neutral to the entity wearing this item, and will not
	 * attack on sight. Note: This does not prevent Piglins from becoming hostile due to other actions, nor does it make Piglins that are already hostile stop being so.
	 *
	 * @param wearer The entity wearing this ItemStack
	 *
	 * @return True if piglins are neutral to players wearing this item in an armor slot
	 */
	default boolean makesPiglinsNeutral(LivingEntity wearer)
	{
		return self().getItem().makesPiglinsNeutral(self(), wearer);
	}

	/**
	 * Whether this Item can be used to hide player head for enderman.
	 *
	 * @param player         The player watching the enderman
	 * @param endermanEntity The enderman that the player look
	 * @return true if this Item can be used.
	 */
	default boolean isEnderMask(PlayerEntity player, EndermanEntity endermanEntity) {
		return self().getItem().isEnderMask(self(), player, endermanEntity);
	}

}