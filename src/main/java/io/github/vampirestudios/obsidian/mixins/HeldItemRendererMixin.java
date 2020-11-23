package io.github.vampirestudios.obsidian.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
	@Redirect(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
	private boolean updateAttackType(ItemStack itemStack, Item item) {
		return itemStack.isOf(Items.CROSSBOW) || item instanceof CrossbowItem;
	}

	@Redirect(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
	private boolean renderItem(ItemStack itemStack, Item item) {
		if (itemStack.getItem() instanceof CrossbowItem) {
			return true; // Return crossbow for rendering
		}

		if (itemStack.getItem() instanceof BowItem) {
			return true; // Return bow for rendering
		}

		return itemStack.isOf(Items.CROSSBOW) || item instanceof CrossbowItem;
	}
}