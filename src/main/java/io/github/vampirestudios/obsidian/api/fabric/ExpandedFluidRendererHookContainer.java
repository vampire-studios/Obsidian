package io.github.vampirestudios.obsidian.api.fabric;

import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

public class ExpandedFluidRendererHookContainer {
	public BlockRenderView view;
	public BlockPos pos;
	public FluidState state;
	public ExpandedFluidRenderHandler handler;
	public final Sprite[] sprites = new Sprite[2];
	public Sprite overlay;
	public boolean hasOverlay;

	public void getSprites(BlockRenderView world, BlockPos pos, FluidState fluid) {
		if (handler != null) {
			Sprite[] sprites = handler.getFluidSprites(world, pos, state);

			this.sprites[0] = sprites[0];
			this.sprites[1] = sprites[1];

			if (sprites.length > 2) {
				hasOverlay = true;
				overlay = sprites[2];
			}
		} else {
			hasOverlay = false;
		}
	}

	public void clear() {
		view = null;
		pos = null;
		state = null;
		handler = null;
		sprites[0] = null;
		sprites[1] = null;
		overlay = null;
		hasOverlay = false;
	}
}