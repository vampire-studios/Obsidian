package io.github.vampirestudios.obsidian.api;

import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public interface IForgeItem {
	private Item self() {
		return (Item) this;
	}

	/**
	 * Allow the item one last chance to modify its name used for the tool highlight
	 * useful for adding something extra that can't be removed by a user in the
	 * displayed name, such as a mode of operation.
	 *
	 * @param item        the ItemStack for the item.
	 * @param displayName the name that will be displayed unless it is changed in
	 *                    this method.
	 */
	default Text getHighlightTip(ItemStack item, Text displayName) {
		return displayName;
	}

	/**
	 * Called by Piglins when checking to see if they will give an item or something in exchange for this item.
	 *
	 * @return True if this item can be used as "currency" by piglins
	 */
	default boolean isPiglinCurrency(ItemStack stack) {
		return stack.getItem() == PiglinBrain.BARTERING_ITEM;
	}

	/**
	 * Called by Piglins to check if a given item prevents hostility on sight. If this is true the Piglins will be neutral to the entity wearing this item, and will not
	 * attack on sight. Note: This does not prevent Piglins from becoming hostile due to other actions, nor does it make Piglins that are already hostile stop being so.
	 *
	 * @param wearer The entity wearing this ItemStack
	 * @return True if piglins are neutral to players wearing this item in an armor slot
	 */
	default boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
		return stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getMaterial() == ArmorMaterials.GOLD;
	}

	/**
	 * Determines the amount of durability the mending enchantment
	 * will repair, on average, per point of experience.
	 */
	default float getXpRepairRatio(ItemStack stack) {
		return 2f;
	}

	/**
	 * Allow or forbid the specific book/item combination as an anvil enchant
	 *
	 * @param stack The item
	 * @param book  The book
	 * @return if the enchantment is allowed
	 */
	default boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return true;
	}

	/**
	 * ItemStack sensitive version of {@link Item#getEnchantability()}.
	 *
	 * @param stack The ItemStack
	 * @return the enchantment value
	 */
	default int getEnchantmentValue(ItemStack stack) {
		return self().getEnchantability();
	}

	/**
	 * Checks whether an item can be enchanted with a certain enchantment. This
	 * applies specifically to enchanting an item in the enchanting table and is
	 * called when retrieving the list of possible enchantments for an item.
	 * Enchantments may additionally (or exclusively) be doing their own checks in
	 * {@link Enchantment#canApplyAtEnchantingTable(ItemStack)};
	 * check the individual implementation for reference. By default this will check
	 * if the enchantment type is valid for this item type.
	 *
	 * @param stack       the item stack to be enchanted
	 * @param enchantment the enchantment to be applied
	 * @return true if the enchantment can be applied to this item
	 */
	default boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return enchantment.type.isAcceptableItem(stack.getItem());
	}

	/**
	 * Gets the level of the enchantment currently present on the stack. By default, returns the enchantment level present in NBT.
	 * Most enchantment implementations rely upon this method.
	 *
	 * @param stack       the item stack being checked
	 * @param enchantment the enchantment being checked for
	 * @return Level of the enchantment, or 0 if not present
	 */
	default int getEnchantmentLevel(ItemStack stack, Enchantment enchantment) {
		return IForgeEnchantmentHelper.getTagEnchantmentLevel(enchantment, stack);
	}

	/**
	 * Whether this Item can be used to hide player head for enderman.
	 *
	 * @param stack          the ItemStack
	 * @param player         The player watching the enderman
	 * @param endermanEntity The enderman that the player look
	 * @return true if this Item can be used to hide player head for enderman
	 */
	default boolean isEnderMask(ItemStack stack, PlayerEntity player, EndermanEntity endermanEntity) {
		return stack.getItem() == Blocks.CARVED_PUMPKIN.asItem();
	}

	/**
	 * Get the tooltip parts that should be hidden by default on the given stack if the {@code HideFlags} tag is not set.
	 *
	 * @param stack the stack
	 * @return the default hide flags
	 * @see ItemStack.TooltipSection
	 */
	default int getDefaultTooltipHideFlags(@NotNull ItemStack stack) {
		return 0;
	}

}