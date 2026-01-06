package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.minecraft.ScopeComponent;
import io.github.vampirestudios.obsidian.registry.OItemComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {

    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    private void yourmod$getUseAnimation(
        ItemStack stack,
        CallbackInfoReturnable<ItemUseAnimation> cir
    ) {
        ScopeComponent scope = stack.get(OItemComponents.SCOPE);
        if (scope != null) {
            cir.setReturnValue(ItemUseAnimation.SPYGLASS);
        }
    }

    @Inject(method = "getUseDuration", at = @At("HEAD"), cancellable = true)
    private void yourmod$getUseDuration(
        ItemStack stack,
        LivingEntity user,
        CallbackInfoReturnable<Integer> cir
    ) {
        ScopeComponent scope = stack.get(OItemComponents.SCOPE);
        if (scope != null) {
            cir.setReturnValue(scope.useDurationTicks());
        }
    }
}
