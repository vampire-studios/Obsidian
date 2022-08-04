package io.github.vampirestudios.obsidian.mixins.client;

import io.github.vampirestudios.obsidian.animation.AnimationManager;
import io.github.vampirestudios.obsidian.animation.HasAnimationManager;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRendererFactory.Context.class)
public class EntityRendererFactoryContextMixin implements HasAnimationManager {
	@Unique
	private AnimationManager quilt$animationManager;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void createAnimationManager(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer, BlockRenderManager blockRenderManager, HeldItemRenderer heldItemRenderer, ResourceManager resourceManager, EntityModelLoader entityModelLoader, TextRenderer textRenderer, CallbackInfo ci) {
		this.quilt$animationManager = entityRenderDispatcher.getAnimationManager();
	}
	@Override
	public AnimationManager getAnimationManager() {
		return quilt$animationManager;
	}
}