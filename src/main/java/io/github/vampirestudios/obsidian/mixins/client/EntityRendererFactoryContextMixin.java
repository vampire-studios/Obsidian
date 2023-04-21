package io.github.vampirestudios.obsidian.mixins.client;

import io.github.vampirestudios.obsidian.animation.AnimationManager;
import io.github.vampirestudios.obsidian.animation.HasAnimationManager;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRendererProvider.Context.class)
public class EntityRendererFactoryContextMixin implements HasAnimationManager {
	@Unique
	private AnimationManager quilt$animationManager;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void createAnimationManager(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer, BlockRenderDispatcher blockRenderManager, ItemInHandRenderer heldItemRenderer, ResourceManager resourceManager, EntityModelSet entityModelLoader, Font textRenderer, CallbackInfo ci) {
		this.quilt$animationManager = entityRenderDispatcher.getAnimationManager();
	}
	@Override
	public AnimationManager getAnimationManager() {
		return quilt$animationManager;
	}
}