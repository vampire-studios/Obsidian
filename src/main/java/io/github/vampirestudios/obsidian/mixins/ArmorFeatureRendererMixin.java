/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
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

package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.ArmorTextureRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(ArmorFeatureRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class ArmorFeatureRendererMixin extends FeatureRenderer {
	@Shadow
	@Final
	private static Map<String, Identifier> ARMOR_TEXTURE_CACHE;

	public ArmorFeatureRendererMixin(FeatureRendererContext context) {
		super(context);
	}

	@Unique
	private LivingEntity storedEntity;
	@Unique
	private EquipmentSlot storedSlot;

	@Inject(method = "render", at = @At("HEAD"))
	private void storeEntity(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
		// We store the living entity wearing the armor before we render
		this.storedEntity = livingEntity;
	}

	@Inject(method = "renderArmor", at = @At("HEAD"))
	private void storeSlot(MatrixStack matrices, VertexConsumerProvider vertexConsumers, LivingEntity livingEntity, EquipmentSlot slot, int i, BipedEntityModel bipedEntityModel, CallbackInfo ci) {
		// We store the current armor slot that is rendering before we render each armor piece
		this.storedSlot = slot;
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void removeStored(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, LivingEntity livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
		// We remove the stored data after we render
		this.storedEntity = null;
		this.storedSlot = null;
	}

	@Inject(method = "getArmorTexture", at = @At("HEAD"), cancellable = true)
	private void getArmorTexture(ArmorItem armorItem, boolean secondLayer, /* @Nullable */ String suffix, CallbackInfoReturnable<Identifier> cir) {
		String model = ArmorTextureRegistry.getArmorTexture(storedEntity, storedEntity.getEquippedStack(storedSlot), storedSlot, secondLayer, suffix);

		if (model != null) {
			cir.setReturnValue(ARMOR_TEXTURE_CACHE.computeIfAbsent(model, Identifier::new));
		}
	}

}