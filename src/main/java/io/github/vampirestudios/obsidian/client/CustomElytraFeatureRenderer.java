package io.github.vampirestudios.obsidian.client;

import io.github.vampirestudios.obsidian.api.obsidian.item.Elytra;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class CustomElytraFeatureRenderer<T extends LivingEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
	private final ElytraEntityModel<T> elytraEntityModel;
	private Item elytraItem;
	private Elytra elytra;

	public CustomElytraFeatureRenderer(Item elytraItem, Elytra elytra, FeatureRendererContext<T, M> context, EntityModelLoader entityModelLoader) {
		super(context);
		this.elytraItem = elytraItem;
		this.elytra = elytra;
		this.elytraEntityModel = new ElytraEntityModel(entityModelLoader.getModelPart(EntityModelLayers.ELYTRA));
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
		ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.CHEST);
		if (itemStack.isOf(elytraItem)) {
			Identifier identifier4;
			if (livingEntity instanceof AbstractClientPlayerEntity) {
				AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)livingEntity;
				if (abstractClientPlayerEntity.canRenderElytraTexture() && abstractClientPlayerEntity.getElytraTexture() != null) {
					identifier4 = abstractClientPlayerEntity.getElytraTexture();
				} else if (abstractClientPlayerEntity.canRenderCapeTexture() && abstractClientPlayerEntity.getCapeTexture() != null && abstractClientPlayerEntity.isPartVisible(PlayerModelPart.CAPE) && elytra.shouldRenderCapeTexture) {
					identifier4 = abstractClientPlayerEntity.getCapeTexture();
				} else {
					System.out.println(abstractClientPlayerEntity.getModel());
					identifier4 = elytra.texture;
				}
			} else {
				identifier4 = elytra.texture;
			}

			matrixStack.push();
			matrixStack.translate(0.0D, 0.0D, 0.125D);
			this.getContextModel().copyStateTo(this.elytraEntityModel);
			this.elytraEntityModel.setAngles(livingEntity, f, g, j, k, l);
			VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumerProvider, RenderLayer.getArmorCutoutNoCull(identifier4), false, itemStack.hasGlint());
			this.elytraEntityModel.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
			matrixStack.pop();
		}
	}

}