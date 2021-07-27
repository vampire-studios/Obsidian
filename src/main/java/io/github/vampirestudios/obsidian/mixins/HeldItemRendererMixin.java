package io.github.vampirestudios.obsidian.mixins;

import net.fabricmc.fabric.api.item.v1.bow.FabricBowExtensions;
import net.fabricmc.fabric.api.item.v1.crossbow.FabricCrossbowExtensions;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Redirect(method = "getHandRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 0))
    private static boolean ob_renderItemBow(ItemStack itemStack, Item item) {
        return itemStack.getItem() instanceof FabricBowExtensions;
    }

    @Redirect(method = "getHandRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 1))
    private static boolean ob_renderItemBow2(ItemStack itemStack, Item item) {
        return itemStack.getItem() instanceof FabricBowExtensions;
    }

    @Redirect(method = "getHandRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 2))
    private static boolean ob_renderItemCrossbow(ItemStack itemStack, Item item) {
        return itemStack.getItem() instanceof FabricCrossbowExtensions;
    }

    @Redirect(method = "getHandRenderType", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 3))
    private static boolean ob_renderItemCrossbow2(ItemStack itemStack, Item item) {
        return itemStack.getItem() instanceof FabricCrossbowExtensions;
    }

    @Redirect(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private boolean ob_renderFirstPersonItem(ItemStack itemStack, Item item) {
        return itemStack.getItem() instanceof FabricCrossbowExtensions;
    }
}