package io.github.vampirestudios.obsidian.mixins.fabric;

import io.github.vampirestudios.obsidian.ExpandedFluidRenderHandlerRegistryImpl;
import io.github.vampirestudios.obsidian.api.fabric.ExpandedFluidRenderHandler;
import io.github.vampirestudios.obsidian.api.fabric.ExpandedFluidRendererHookContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FluidRenderer.class)
public class MixinFluidRenderer {
	@Shadow
	private Sprite[] lavaSprites;
	@Shadow
	private Sprite[] waterSprites;
	@Shadow
	private Sprite waterOverlaySprite;

	private final ThreadLocal<ExpandedFluidRendererHookContainer> obsidian_renderHandler = ThreadLocal.withInitial(ExpandedFluidRendererHookContainer::new);
	private final ThreadLocal<Boolean> fabric_customRendering = ThreadLocal.withInitial(() -> false);
	private final ThreadLocal<Block> fabric_neighborBlock = new ThreadLocal<>();

	@Inject(at = @At("RETURN"), method = "onResourceReload")
	public void onResourceReloadReturn(CallbackInfo info) {
		FluidRenderer self = FluidRenderer.class.cast(this);
		ExpandedFluidRenderHandlerRegistryImpl.INSTANCE.onFluidRendererReload(self, waterSprites, lavaSprites, waterOverlaySprite);
	}

	@Inject(at = @At("HEAD"), method = "render", cancellable = true)
	public void tesselate(BlockRenderView view, BlockPos pos, VertexConsumer vertexConsumer, FluidState state, CallbackInfoReturnable<Boolean> info) {
		if (!fabric_customRendering.get()) {
			// Prevent recursively looking up custom fluid renderers when default behaviour is being invoked
			fabric_customRendering.set(true);
			tessellateViaHandler(view, pos, vertexConsumer, state, info);
			fabric_customRendering.set(false);
		}

		if (info.isCancelled()) {
			return;
		}

		ExpandedFluidRendererHookContainer ctr = obsidian_renderHandler.get();
		ctr.getSprites(view, pos, state);
	}

	@Unique
	private void tessellateViaHandler(BlockRenderView view, BlockPos pos, VertexConsumer vertexConsumer, FluidState state, CallbackInfoReturnable<Boolean> info) {
		ExpandedFluidRendererHookContainer ctr = obsidian_renderHandler.get();
		ExpandedFluidRenderHandler handler = ExpandedFluidRenderHandlerRegistryImpl.INSTANCE.getOverride(state.getFluid());

		ctr.view = view;
		ctr.pos = pos;
		ctr.state = state;
		ctr.handler = handler;

		if (handler != null) {
			info.setReturnValue(handler.renderFluid(pos, view, vertexConsumer, state));
		}
	}

	@Inject(at = @At("RETURN"), method = "render")
	public void tesselateReturn(BlockRenderView view, BlockPos pos, VertexConsumer vertexConsumer, FluidState state, CallbackInfoReturnable<Boolean> info) {
		obsidian_renderHandler.get().clear();
	}

	@ModifyVariable(at = @At(value = "INVOKE", target = "net/minecraft/client/render/block/FluidRenderer.isSameFluid(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/minecraft/fluid/FluidState;)Z"), method = "render", ordinal = 0)
	public boolean modLavaCheck(boolean chk) {
		// First boolean local is set by vanilla according to 'matches lava'
		// but uses the negation consistent with 'matches water'
		// for determining if special water sprite should be used behind glass.

		// Has other uses but those are overridden by this mixin and have
		// already happened by the time this hook is called

		// If this fluid has an overlay texture, set this boolean too false
		final ExpandedFluidRendererHookContainer ctr = obsidian_renderHandler.get();
		return !ctr.hasOverlay;
	}

	@ModifyVariable(at = @At(value = "INVOKE", target = "net/minecraft/client/render/block/FluidRenderer.isSameFluid(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/minecraft/fluid/FluidState;)Z"), method = "render", ordinal = 0)
	public Sprite[] modSpriteArray(Sprite[] chk) {
		ExpandedFluidRendererHookContainer ctr = obsidian_renderHandler.get();
		return ctr.handler != null ? ctr.sprites : chk;
	}

	// Redirect redirects all 'waterOverlaySprite' gets in 'render' to this method, this is correct
	@Redirect(at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lnet/minecraft/client/render/block/FluidRenderer;waterOverlaySprite:Lnet/minecraft/client/texture/Sprite;"), method = "render")
	public Sprite modWaterOverlaySprite(FluidRenderer self) {
		ExpandedFluidRendererHookContainer ctr = obsidian_renderHandler.get();
		return ctr.handler != null && ctr.hasOverlay ? ctr.overlay : waterOverlaySprite;
	}

	@ModifyVariable(at = @At(value = "CONSTANT", args = "intValue=16", ordinal = 0, shift = At.Shift.BEFORE), method = "render", ordinal = 0)
	public int modTintColor(int chk) {
		ExpandedFluidRendererHookContainer ctr = obsidian_renderHandler.get();
		return ctr.handler != null ? ctr.handler.getFluidColor(ctr.view, ctr.pos, ctr.state) : chk;
	}

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;"), method = "render")
	public Block getOverlayBlock(BlockState state) {
		Block block = state.getBlock();
		fabric_neighborBlock.set(block);

		// An if-statement follows, we don't want this anymore and 'null' makes
		// its condition always false (due to instanceof)
		return null;
	}

	@ModifyVariable(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;getBlock()Lnet/minecraft/block/Block;", shift = At.Shift.BY, by = 2), method = "render", ordinal = 0)
	public Sprite modSideSpriteForOverlay(Sprite chk) {
		Block block = fabric_neighborBlock.get();

		if (ExpandedFluidRenderHandlerRegistryImpl.INSTANCE.isBlockTransparent(block)) {
			ExpandedFluidRendererHookContainer ctr = obsidian_renderHandler.get();
			return ctr.handler != null && ctr.hasOverlay ? ctr.overlay : waterOverlaySprite;
		}

		return chk;
	}
}