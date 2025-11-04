/*
package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.CosmeticsData;
import io.github.vampirestudios.obsidian.IEntityDataSaver;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Redirect(
            method = "method_30120",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;",
                    ordinal = 0
            )
    )
    ItemStack modifyHeadSlotItem (ItemStack instance, List list, EquipmentSlot slot, ItemStack stack) {
        if ((LivingEntity) (Object) this instanceof Player player) {
            if(slot.getIndex() == 3) {
                ItemStack cosmeticsIS = CosmeticsData.getHeadCosmetics((IEntityDataSaver) player);
                if (cosmeticsIS != ItemStack.EMPTY) {
                    return cosmeticsIS;
                }
            }
        }
        return instance.copy();
    }
}*/
