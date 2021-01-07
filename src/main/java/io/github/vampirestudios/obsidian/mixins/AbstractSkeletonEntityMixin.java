package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.BowInterface;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractSkeletonEntity.class)
public class AbstractSkeletonEntityMixin {
	@Redirect(method = "updateAttackType", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
	private Item ob_updateAttackType(ItemStack itemStack) {
		return itemStack.getItem() instanceof BowInterface ? Items.BOW : itemStack.getItem();
	}
}