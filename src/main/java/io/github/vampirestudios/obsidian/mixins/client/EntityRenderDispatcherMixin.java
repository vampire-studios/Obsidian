package io.github.vampirestudios.obsidian.mixins.client;

import io.github.vampirestudios.obsidian.animation.AnimationManager;
import io.github.vampirestudios.obsidian.animation.HasAnimationManager;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin implements HasAnimationManager {
	@Unique
	private AnimationManager quilt$animationManager;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void createAnimationManager(MinecraftClient minecraftClient, TextureManager textureManager, ItemRenderer itemRenderer, BlockRenderManager blockRenderManager, TextRenderer textRenderer, GameOptions gameOptions, EntityModelLoader entityModelLoader, CallbackInfo ci) {
		this.quilt$animationManager = new AnimationManager();
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(this.quilt$animationManager);
	}

	@Override
	public AnimationManager getAnimationManager() {
		return quilt$animationManager;
	}
}