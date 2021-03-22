package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DrownedEntityModel.class)
public class DrownedEntityModelMixin {
	@Redirect(method = "animateModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
	public Item ob_getItem(ItemStack stack) {
		return stack.getItem() instanceof TridentInterface ? Items.TRIDENT : stack.getItem();
	}
}