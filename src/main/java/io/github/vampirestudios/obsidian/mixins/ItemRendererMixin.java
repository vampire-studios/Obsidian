package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
	@Shadow
	@Final
	private ItemModels models;
	@Shadow
	@Final
	private BuiltinModelItemRenderer builtinModelItemRenderer;

	@Redirect(method = "getHeldItemModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
	public boolean ob_getHeldItemModel(ItemStack itemStack, Item item) {
		return itemStack.getItem() instanceof TridentInterface;
	}

	@Shadow
	protected abstract void renderBakedItemModel(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertexConsumer4);

	@Inject(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V", at = @At(value = "HEAD"), cancellable = true)
	public void ob_renderItem(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo info) {
		if (!stack.isEmpty() && stack.getItem() instanceof TridentInterface) {
			matrices.push();
			boolean bl = renderMode == ModelTransformation.Mode.GUI || renderMode == ModelTransformation.Mode.GROUND || renderMode == ModelTransformation.Mode.FIXED;

			if (bl) {
				model = models.getModelManager().getModel(((TridentInterface) stack.getItem()).getInventoryModelIdentifier());
			}

			model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);
			matrices.translate(-0.5D, -0.5D, -0.5D);

			if (model.isBuiltin() || stack.getItem() instanceof TridentInterface && !bl) {
				builtinModelItemRenderer.render(stack, renderMode, matrices, vertexConsumers, light, overlay);
			} else {
				RenderLayer renderLayer = RenderLayers.getItemLayer(stack, true);
				VertexConsumer vertexConsumer4;
				vertexConsumer4 = ItemRenderer.getItemGlintConsumer(vertexConsumers, renderLayer, true, stack.hasGlint());
				renderBakedItemModel(model, stack, light, overlay, matrices, vertexConsumer4);
			}

			matrices.pop();
			info.cancel();
		}
	}
} 