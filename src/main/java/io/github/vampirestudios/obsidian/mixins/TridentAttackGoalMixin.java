package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.TridentInterface;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Allow Drowneds to start their attack goal
@Mixin(DrownedEntity.TridentAttackGoal.class)
public class TridentAttackGoalMixin {
	@Redirect(method = "canStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
	public boolean ob_canStart(ItemStack itemStack, Item item) {
		return itemStack.getItem() instanceof TridentInterface;
	}
}