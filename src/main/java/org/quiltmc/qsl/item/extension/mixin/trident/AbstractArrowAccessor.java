package org.quiltmc.qsl.item.extension.mixin.trident;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractArrow.class)
public interface AbstractArrowAccessor {
    @Accessor("pickupItemStack")
    ItemStack getPickupItemStack();

    @Accessor("pickupItemStack")
    void setPickupItemStack(ItemStack stack);
}