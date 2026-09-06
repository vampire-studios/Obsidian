package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.obsidian.palette.PaletteApplication;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Applies a palette by clicking one item onto another in an inventory, the way bundles take items.
 *
 * <p>Right-click a chroma set held on the cursor onto an item in a slot — or right-click a set
 * sitting in a slot while carrying the item to paint. Both directions work, so it does not matter
 * which one you picked up first.</p>
 */
@Mixin(Item.class)
public class ItemPaletteApplicatorMixin {

	/** Cursor carries the applicator, the clicked slot holds the target. */
	@Inject(method = "overrideStackedOnOther", at = @At("HEAD"), cancellable = true)
	private void obsidian$paintSlotFromCursor(ItemStack carried, Slot slot, ClickAction action, Player player,
	                                          CallbackInfoReturnable<Boolean> cir) {
		if (action != ClickAction.SECONDARY || !slot.allowModification(player)) return;

		if (PaletteApplication.applyByClick(carried, slot.getItem(), player)) cir.setReturnValue(true);
	}

	/** Cursor carries the target, the clicked slot holds the applicator. */
	@Inject(method = "overrideOtherStackedOnMe", at = @At("HEAD"), cancellable = true)
	private void obsidian$paintCursorFromSlot(ItemStack inSlot, ItemStack carried, Slot slot, ClickAction action,
	                                          Player player, SlotAccess access, CallbackInfoReturnable<Boolean> cir) {
		if (action != ClickAction.SECONDARY || !slot.allowModification(player)) return;

		if (PaletteApplication.applyByClick(inSlot, carried, player)) cir.setReturnValue(true);
	}
}
