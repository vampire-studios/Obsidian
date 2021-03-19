package io.github.vampirestudios.obsidian.mixins;

import com.mojang.datafixers.util.Pair;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ShieldItemImpl;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BannerPattern;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BuiltinModelItemRenderer.class)
public class BuiltinModelItemRendererMixin {

	@Shadow
	private ShieldEntityModel modelShield;

	@Inject(at = @At("HEAD"), method = "render", cancellable = true)
	private void render(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrixStack, VertexConsumerProvider consumerProvider, int light, int overlay,
						CallbackInfo info) {
		Item item = stack.getItem();
		if (item instanceof ShieldItemImpl) {
			boolean bl = stack.getSubTag("BlockEntityTag") != null;
			matrixStack.push();
			matrixStack.scale(1.0F, -1.0F, -1.0F);
			SpriteIdentifier spriteIdentifier;
			if (((ShieldItemImpl) item).shieldItem.can_have_banner)
				spriteIdentifier = bl ? new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, ((ShieldItemImpl) item).shieldItem.shieldBase)
						: new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, ((ShieldItemImpl) item).shieldItem.shieldBaseNoPattern);
			else
				spriteIdentifier = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, ((ShieldItemImpl) item).shieldItem.shieldBaseNoPattern);
			VertexConsumer vertexConsumer = spriteIdentifier.getSprite()
					.getTextureSpecificVertexConsumer(ItemRenderer.getDirectItemGlintConsumer(consumerProvider,
							modelShield.getLayer(spriteIdentifier.getAtlasId()), true, stack.hasGlint()));
			modelShield.getHandle().render(matrixStack, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
			if (bl && ((ShieldItemImpl) item).shieldItem.can_have_banner) {
				List<Pair<BannerPattern, DyeColor>> list = BannerBlockEntity.getPatternsFromNbt(ShieldItem.getColor(stack),
						BannerBlockEntity.getPatternListTag(stack));
				BannerBlockEntityRenderer.renderCanvas(matrixStack, consumerProvider, light, overlay,
						modelShield.getPlate(), spriteIdentifier, false, list, stack.hasGlint());
			} else {
				modelShield.getPlate().render(matrixStack, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
			}

			matrixStack.pop();
		}

	}

}
