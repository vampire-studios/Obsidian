package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.TridentInterface;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Allow Drowneds to start their attack goal
@Mixin(DrownedEntity.TridentAttackGoal.class)
public class TridentAttackGoalMixin {
	@Redirect(method = "canStart", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;"))
	public Item ob_canStart(ItemStack stack) {
		return stack.getItem() instanceof TridentInterface ? Items.TRIDENT : stack.getItem();
	}
}