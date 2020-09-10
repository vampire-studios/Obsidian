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
	@Redirect(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
	private Item renderFirstPersonItem(ItemStack itemStack) {
		if (itemStack.getItem() instanceof CrossbowItem) {
			return Items.CROSSBOW; // Return true to invoke crossbow rendering path
		}

		return itemStack.getItem();
	}

	@Redirect(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
	private Item renderItem(ItemStack itemStack) {
		if (itemStack.getItem() instanceof CrossbowItem) {
			return Items.CROSSBOW; // Return crossbow for rendering
		}

		if (itemStack.getItem() instanceof BowItem) {
			return Items.BOW; // Return bow for rendering
		}

		return itemStack.getItem();
	}
}