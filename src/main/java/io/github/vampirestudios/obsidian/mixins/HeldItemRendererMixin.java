package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.BowInterface;
import io.github.vampirestudios.obsidian.api.CrossbowInterface;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
	@Redirect(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
	private Item ob_renderFirstPersonItem(ItemStack heldItem) {
		if (heldItem.getItem() instanceof CrossbowInterface) {
			return Items.CROSSBOW; // Return true to invoke crossbow rendering path
		}

		return heldItem.getItem();
	}

	@Redirect(method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
	private Item ob_renderItem(ItemStack heldItem) {
		if (heldItem.getItem() instanceof CrossbowInterface) {
			return Items.CROSSBOW; // Return crossbow for rendering
		}

		if (heldItem.getItem() instanceof BowInterface) {
			return Items.BOW; // Return bow for rendering
		}

		return heldItem.getItem();
	}
}