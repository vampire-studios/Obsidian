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

package org.quiltmc.qsl.fluid.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FluidState;
import org.quiltmc.qsl.fluid.impl.CameraExtensions;
import org.quiltmc.qsl.fluid.api.QuiltFlowableFluidExtensions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public class BackgroundRendererMixin {

	@Shadow
	private static float fogRed;
	@Shadow
	private static float fogGreen;
	@Shadow
	private static float fogBlue;
	@Shadow
	private static long biomeChangedTime = -1L;

	@Inject(method = "setupColor",
			at = @At("HEAD"),
			cancellable = true)
	private static void render(Camera camera, float tickDelta, ClientLevel world, int i, float f, CallbackInfo ci) {
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
	}

	@Inject(method = "setupFog",
			at = @At("HEAD"),
			cancellable = true)
	private static void applyFog(Camera camera, FogRenderer.FogMode fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
		//Get the fluid that submerged the camera
		FluidState fluidState = ((CameraExtensions) camera).quilt$getSubmergedFluidState();

		//If this is an instance of FabricFlowableFluid interface...
		if (fluidState.getType() instanceof QuiltFlowableFluidExtensions fluid) {

			//Get the start and end parameters and apply them, then return.
			Entity entity = camera.getEntity();
			RenderSystem.setShaderFogStart(fluid.getFogStart(fluidState, entity, viewDistance));
			RenderSystem.setShaderFogEnd(fluid.getFogEnd(fluidState, entity, viewDistance));

			ci.cancel();
		}
	}

}