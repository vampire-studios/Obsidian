package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractSkeletonEntity.class)
public class AbstractSkeletonEntityMixin {
	@Redirect(method = "updateAttackType", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
	private boolean updateAttackType(ItemStack itemStack, Item item) {
		return itemStack.isOf(Items.BOW) || item instanceof BowItem;
	}
}