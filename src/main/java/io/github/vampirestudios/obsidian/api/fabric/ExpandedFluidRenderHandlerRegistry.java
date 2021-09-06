package io.github.vampirestudios.obsidian.api.fabric;

import io.github.vampirestudios.obsidian.ExpandedFluidRenderHandlerRegistryImpl;
import net.minecraft.block.Block;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.TransparentBlock;
import net.minecraft.fluid.Fluid;

public interface ExpandedFluidRenderHandlerRegistry {
	ExpandedFluidRenderHandlerRegistry INSTANCE = ExpandedFluidRenderHandlerRegistryImpl.INSTANCE;

	/**
	 * Get a {@link ExpandedFluidRenderHandler} for a given Fluid.
	 * Supports vanilla and Fabric fluids.
	 *
	 * @param fluid The Fluid.
	 * @return The FluidRenderHandler.
	 */
	ExpandedFluidRenderHandler get(Fluid fluid);

	/**
	 * Register a {@link ExpandedFluidRenderHandler} for a given Fluid.
	 *
	 * @param fluid The Fluid.
	 * @param renderer The FluidRenderHandler.
	 */
	void register(Fluid fluid, ExpandedFluidRenderHandler renderer);

	/**
	 * Registers whether a block is transparent or not. When a block is
	 * tranparent, the flowing fluid texture to the sides of that block is
	 * replaced by a special overlay texture. This happens by default with glass
	 * and leaves, and hence blocks inheriting {@link TransparentBlock} and
	 * {@link LeavesBlock} are by default transparent. Use this method to
	 * override the default behavior for a block.
	 *
	 * @param block The block to register transparency for.
	 * @param transparent Whether the block is transparent (e.g. gets the
	 * overlay textures) or not.
	 */
	void setBlockTransparency(Block block, boolean transparent);

	/**
	 * Looks up whether a block is transparent and gets a fluid overlay texture
	 * instead of a falling fluid texture. If transparency is registered for a
	 * block (via {@link #setBlockTransparency}), this method returns that
	 * registered transparency. Otherwise, this method returns whether the block
	 * is a subclass of {@link TransparentBlock} or {@link LeavesBlock}.
	 *
	 * @param block The block to get transparency for.
	 * @return Whether the block is transparent (e.g. gets the overlay textures)
	 * or not.
	 */
	boolean isBlockTransparent(Block block);

}
