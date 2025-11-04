package org.quiltmc.qsl.item.extension.mixin.trident.client;

import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ThrownTridentRenderer.class)
public class TridentEntityRendererMixin {
//    @Inject(method = "getTex", at = @At(value = "HEAD"), cancellable = true)
//    public void getTextureMixin(ThrownTrident entity, CallbackInfoReturnable<ResourceLocation> cir) {
//        if(((AbstractArrowAccessor) entity).getPickupItemStack().getItem() instanceof TridentExtensions tridentItem) {
//            cir.setReturnValue(tridentItem.getRenderTexture());
//        }
//    }
}