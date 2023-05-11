package io.github.vampirestudios.obsidian.mixins.client;

import io.github.vampirestudios.obsidian.client.ClientInit;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBakery.class)
public class ModelLoaderMixin {
//    @Inject(method="<init>", at=@At(value="INVOKE_STRING", target="Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args="ldc=textures"))
//    public void ob_injectINIT(BlockColors blockColors, Profiler profiler, Map map, Map map2, CallbackInfo ci) {
//        for(ModelIdentifier modelIdentifier : ClientInit.customModels) {
//            this.addModel(modelIdentifier);
//        }
//    }
    
    @Inject(method="loadModel", at=@At("HEAD"), cancellable=true)
    public void ob_injectLoadModel(ResourceLocation id, CallbackInfo info) {
        if(id instanceof ModelResourceLocation modelIdentifier) {
            if (ClientInit.customModels.contains(modelIdentifier)) {
                BlockModel jsonUnbakedModel = this.loadBlockModel(modelIdentifier);
                this.cacheAndQueueDependencies(modelIdentifier, jsonUnbakedModel);
                info.cancel();
            }
        }
    }
    
    @Shadow
    private void loadTopLevel(ModelResourceLocation modelId) { }
    
    @Shadow
    private BlockModel loadBlockModel(ResourceLocation id) { return null; }
    
    @Shadow
    private void cacheAndQueueDependencies(ResourceLocation id, UnbakedModel unbakedModel) { }
}