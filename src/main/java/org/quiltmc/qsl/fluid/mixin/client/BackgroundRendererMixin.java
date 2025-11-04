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
 *//*


package org.quiltmc.qsl.fluid.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogParameters;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FluidState;
import org.joml.Vector4f;
import org.quiltmc.qsl.fluid.api.QuiltFlowableFluidExtensions;
import org.quiltmc.qsl.fluid.impl.CameraExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class BackgroundRendererMixin {

	@Shadow
	private static long biomeChangedTime = -1L;

	*/
/*@Inject(method = "computeFogColor",
			at = @At("HEAD")
	)
	private static void render(Camera camera, float f, ClientLevel clientLevel, int i, float g, CallbackInfoReturnable<Vector4f> cir) {
		//Get the fluid that submerged the camera
		FluidState fluidState = ((CameraExtensions) camera).quilt$getSubmergedFluidState();

		//If this is an instance of FabricFlowableFluid interface...
		if (fluidState.getType() instanceof QuiltFlowableFluidExtensions fluid) {
			//Get the color of the fog...
			int fogColor = fluid.getFogColor(fluidState, camera.getEntity());
			if (fogColor != -1) { // water color special casing, -1 marks water color
				//This is a hexadecimal color, so we need to get the three "red", "green", and "blue" values.
				fogRed = (fogColor >> 16 & 255) / 255f;
				fogGreen = (fogColor >> 8 & 255) / 255f;
				fogBlue = (fogColor & 255) / 255f;

				//This is for compatibility, just add!
				biomeChangedTime = -1L;

				//Apply the color, then return.
				RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0f);

				ci.cancel();
			}
		}
	}*//*


	@Inject(method = "setupFog",
			at = @At("HEAD")
	)
	private static void applyFog(Camera camera, FogRenderer.FogMode fogMode, Vector4f vector4f, float f, boolean bl, float g, CallbackInfoReturnable<FogParameters> cir) {
		//Get the fluid that submerged the camera
		FluidState fluidState = ((CameraExtensions) camera).quilt$getSubmergedFluidState();

		//If this is an instance of FabricFlowableFluid interface...
		if (fluidState.getType() instanceof QuiltFlowableFluidExtensions fluid) {

			//Get the start and end parameters and apply them, then return.
			Entity entity = camera.getEntity();
//			RenderSystem.setShaderFo(fluid.getFogStart(fluidState, entity, viewDistance));
//			RenderSystem.setShaderFogEnd(fluid.getFogEnd(fluidState, entity, viewDistance));

			cir.cancel();
		}
	}

}*/
