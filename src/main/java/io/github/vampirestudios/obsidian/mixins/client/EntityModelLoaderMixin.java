/*
 * Copyright 2022 QuiltMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.vampirestudios.obsidian.mixins.client;

import io.github.vampirestudios.obsidian.DynamicEntityModelLoader;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityModelSet.class)
public class EntityModelLoaderMixin {
    @Unique
    private DynamicEntityModelLoader quilt$dynamicEntityModelLoader;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void createAnimationManager(CallbackInfo ci) {
//        this.quilt$dynamicEntityModelLoader = new DynamicEntityModelLoader();
//        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this.quilt$dynamicEntityModelLoader);
    }

    @Inject(method = "getModelPart", at = @At("HEAD"), cancellable = true)
    public void returnDynamicModel(ModelLayerLocation layer, CallbackInfoReturnable<ModelPart> cir) {
//        TexturedModelData modelData = quilt$dynamicEntityModelLoader.getModelData(layer);
//        if (modelData != null) {
//            cir.setReturnValue(modelData.createModel());
//        }
    }
}