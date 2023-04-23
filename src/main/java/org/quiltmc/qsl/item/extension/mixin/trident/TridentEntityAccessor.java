package org.quiltmc.qsl.item.extension.mixin.trident;

import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ThrownTrident.class)
public interface TridentEntityAccessor {
    @Accessor("tridentItem")
    ItemStack getTridentStack();

    @Accessor("tridentItem")
    void setTridentStack(ItemStack stack);
}