package io.github.vampirestudios.obsidian.mixins.client;

import io.github.vampirestudios.obsidian.client.ClientInit;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelLoader.class)
public class ModelLoaderMixin {
    @Inject(method="<init>", at=@At(value="INVOKE_STRING", target="Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args="ldc=textures"))
    public void ob_injectINIT(ResourceManager resourceManager, BlockColors blockColors, Profiler profiler, int i, CallbackInfo info) {
        for(ModelIdentifier modelIdentifier : ClientInit.customModels) {
            this.addModel(modelIdentifier);
        }
    }
    
    @Inject(method="loadModel", at=@At("HEAD"), cancellable=true)
    public void ob_injectLoadModel(Identifier id, CallbackInfo info) {
        if(id instanceof ModelIdentifier modelIdentifier) {
            if (ClientInit.customModels.contains(modelIdentifier)) {
                JsonUnbakedModel jsonUnbakedModel = this.loadModelFromJson(modelIdentifier);
                this.putModel(modelIdentifier, jsonUnbakedModel);
                info.cancel();
            }
        }
    }
    
    @Shadow
    private void addModel(ModelIdentifier modelId) { }
    
    @Shadow
    private JsonUnbakedModel loadModelFromJson(Identifier id) { return null; }
    
    @Shadow
    private void putModel(Identifier id, UnbakedModel unbakedModel) { }
}