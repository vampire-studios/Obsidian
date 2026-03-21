/*
package io.github.vampirestudios.obsidian.mixins.client;

import io.github.vampirestudios.obsidian.BlockModelWrapperMultiAtlasFlagAccessor;
import io.github.vampirestudios.obsidian.MultiAtlasState;
import net.minecraft.client.renderer.item.BlockModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.renderer.item.BlockModelWrapper.Unbaked")
public abstract class BlockModelWrapperUnbakedBakeMixin {

    @Inject(method = "bake", at = @At("HEAD"))
    private void obsidian$resetMultiAtlasState(ItemModel.BakingContext bakingContext, CallbackInfoReturnable<ItemModel> cir) {
        MultiAtlasState.reset();
    }

    @Inject(method = "bake", at = @At("RETURN"))
    private void obsidian$tagMultiAtlasModel(ItemModel.BakingContext bakingContext, CallbackInfoReturnable<ItemModel> cir) {
        Object out = cir.getReturnValue();
        if (out instanceof BlockModelWrapper wrapper) {
            ((BlockModelWrapperMultiAtlasFlagAccessor) wrapper).obsidian$setMultiAtlas(MultiAtlasState.isMixed());
        }
    }
}
*/
