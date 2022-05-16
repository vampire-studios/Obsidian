package io.github.vampirestudios.obsidian.mixins.client;

import io.github.vampirestudios.obsidian.api.fabric.BowExtensions;
import io.github.vampirestudios.obsidian.api.fabric.CrossbowExtensions;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = HeldItemRenderer.class, priority = 1100)
public class HeldItemRendererMixin {
    // Make sure that the custom items are rendered in the correct place based on the current swing progress of the hand
    @Redirect(method = "getHandRenderType",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private static boolean ob_renderItem(ItemStack heldItem, Item item) {
        if (item == Items.BOW)
            return heldItem.getItem() instanceof BowExtensions || heldItem.getItem() == Items.BOW; // Return bow for rendering
        if (item == Items.CROSSBOW)
            return heldItem.getItem() instanceof CrossbowExtensions || heldItem.getItem() == Items.CROSSBOW; // Return crossbow for rendering
        return heldItem.isOf(item); // Default behavior
    }

    @Redirect(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 1))
    private boolean ob_renderFirstPersonItem(ItemStack heldItem, Item item) {
        if (heldItem.getItem() instanceof CrossbowExtensions) {
            return true; // Return true to invoke crossbow rendering path
        }

        return heldItem.isOf(item);
    }
}