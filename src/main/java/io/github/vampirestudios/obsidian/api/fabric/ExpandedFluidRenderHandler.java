package io.github.vampirestudios.obsidian.api.fabric;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.vampirestudios.obsidian.ExpandedFluidRenderHandlerRegistryImpl;
import net.minecraft.block.BlockState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

public interface ExpandedFluidRenderHandler {
	/**
	 * Get the sprites for a fluid being rendered at a given position.
	 * For optimal performance, the sprites should be loaded as part of a
	 * resource reload and *not* looked up every time the method is called!
	 *
	 * <p>The "fabric-textures" module contains sprite rendering facilities, which may come in handy here.
	 *
	 * @param view  The world view pertaining to the fluid. May be null!
	 * @param pos   The position of the fluid in the world. May be null!
	 * @param state The current state of the fluid.
	 * @return An array of size two: the first entry contains the "still" sprite,
	 * while the second entry contains the "flowing" sprite.
	 */
	Sprite[] getFluidSprites(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state);

	/**
	 * Get the tint color for a fluid being rendered at a given position.
	 *
	 * <p>Note: As of right now, our hook cannot handle setting a custom alpha
	 * tint here - as such, it must be contained in the texture itself!
	 *
	 * @param view  The world view pertaining to the fluid. May be null!
	 * @param pos   The position of the fluid in the world. May be null!
	 * @param state The current state of the fluid.
	 * @return The tint color of the fluid.
	 */
	default int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
		return -1;
	}

	/**
	 * Tessellate your fluid. This method will be invoked before the default
	 * fluid renderer. By default it will call the default fluid renderer. Call
	 * {@code FluidRenderHandler.super.renderFluid} if you want to render over
	 * the default fluid renderer.
	 *
	 * <p>Note that this method must *only* return {@code true} if at least one
	 * face is tessellated. If no faces are tessellated this method must return
	 * {@code false}.
	 *
	 * @param pos The position in the world, of the fluid to render.
	 * @param world The world the fluid is in
	 * @param vertexConsumer The vertex consumer to tessellate the fluid in.
	 * @param state The fluid state being rendered.
	 */
	default void renderFluid(BlockPos pos, BlockRenderView world, VertexConsumer vertexConsumer, BlockState blockState, FluidState state) {
		ExpandedFluidRenderHandlerRegistryImpl.INSTANCE.renderFluid(pos, world, vertexConsumer, blockState, state);
	}

	/**
	 * Look up your Fluid's sprites from the texture atlas. Called when the
	 * fluid renderer reloads its textures. This is a convenient way of
	 * reloading and does not require an advanced resource manager reload
	 * listener.
	 *
	 * <p>The "fabric-textures" module contains sprite rendering facilities,
	 * which may come in handy here.
	 *
	 * @param textureAtlas The blocks texture atlas, provided for convenience.
	 */
	default void reloadTextures(SpriteAtlasTexture textureAtlas) {
	}

}
