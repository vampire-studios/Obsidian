package io.github.vampirestudios.obsidian.mixins.client;

import io.github.vampirestudios.obsidian.api.TridentInterface;
import io.github.vampirestudios.obsidian.mixins.TridentEntityAccessor;
import net.minecraft.client.render.entity.TridentEntityRenderer;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TridentEntityRenderer.class)
public class TridentEntityRendererMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/TridentEntityRenderer;getTexture(Lnet/minecraft/entity/projectile/TridentEntity;)Lnet/minecraft/util/Identifier;"))
    public Identifier ob_getTextureMixin(TridentEntityRenderer renderer, TridentEntity entity) {
        Item tridentItem = ((TridentEntityAccessor) entity).getTridentStack().getItem();
        return tridentItem instanceof TridentInterface ? ((TridentInterface) tridentItem).getEntityTexture() : TridentEntityRenderer.TEXTURE;
    }
}