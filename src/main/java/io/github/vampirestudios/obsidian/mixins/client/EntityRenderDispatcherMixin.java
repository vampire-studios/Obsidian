package io.github.vampirestudios.obsidian.mixins.client;

import io.github.vampirestudios.obsidian.animation.AnimationManager;
import io.github.vampirestudios.obsidian.animation.HasAnimationManager;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.server.packs.PackType;
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
	private void createAnimationManager(Minecraft minecraftClient, TextureManager textureManager, ItemRenderer itemRenderer, BlockRenderDispatcher blockRenderManager, Font textRenderer, Options gameOptions, EntityModelSet entityModelLoader, CallbackInfo ci) {
		this.quilt$animationManager = new AnimationManager();
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(this.quilt$animationManager);
	}

	@Override
	public AnimationManager getAnimationManager() {
		return quilt$animationManager;
	}
}