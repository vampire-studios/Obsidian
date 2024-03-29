package org.quiltmc.qsl.item.extension.mixin.trident.client;

import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.ThrownTrident;
import org.quiltmc.qsl.item.extension.api.trident.TridentExtensions;
import org.quiltmc.qsl.item.extension.mixin.trident.AbstractArrowAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ThrownTridentRenderer.class)
public class TridentEntityRendererMixin {
    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/projectile/ThrownTrident;)Lnet/minecraft/resources/ResourceLocation;", at = @At(value = "HEAD"), cancellable = true)
    public void getTextureMixin(ThrownTrident entity, CallbackInfoReturnable<ResourceLocation> cir) {
        if(((AbstractArrowAccessor) entity).getPickupItemStack().getItem() instanceof TridentExtensions tridentItem) {
            cir.setReturnValue(tridentItem.getRenderTexture());
        }
    }
}