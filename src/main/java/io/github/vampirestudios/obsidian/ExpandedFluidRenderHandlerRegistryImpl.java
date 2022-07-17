package io.github.vampirestudios.obsidian;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.vampirestudios.obsidian.api.fabric.ExpandedFluidRenderHandler;
import io.github.vampirestudios.obsidian.api.fabric.ExpandedFluidRenderHandlerRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.TransparentBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.biome.BiomeKeys;

import java.util.IdentityHashMap;
import java.util.Map;

public class ExpandedFluidRenderHandlerRegistryImpl implements ExpandedFluidRenderHandlerRegistry {
	public static final ExpandedFluidRenderHandlerRegistryImpl INSTANCE = new ExpandedFluidRenderHandlerRegistryImpl();
	private static final int DEFAULT_WATER_COLOR = BuiltinRegistries.BIOME.get(BiomeKeys.OCEAN).getWaterColor();
	private final Map<Fluid, ExpandedFluidRenderHandler> handlers = new IdentityHashMap<>();
	private final Map<Fluid, ExpandedFluidRenderHandler> modHandlers = new IdentityHashMap<>();
	private final Map<Block, Boolean> overlayBlocks = new IdentityHashMap<>();

	private FluidRenderer fluidRenderer;

	private ExpandedFluidRenderHandlerRegistryImpl() {
	}

	@Override
	public ExpandedFluidRenderHandler get(Fluid fluid) {
		return handlers.get(fluid);
	}

	public ExpandedFluidRenderHandler getOverride(Fluid fluid) {
		return modHandlers.get(fluid);
	}

	@Override
	public void register(Fluid fluid, ExpandedFluidRenderHandler renderer) {
		handlers.put(fluid, renderer);
		modHandlers.put(fluid, renderer);
	}

	@Override
	public void setBlockTransparency(Block block, boolean transparent) {
		overlayBlocks.put(block, transparent);
	}

	@Override
	public boolean isBlockTransparent(Block block) {
		return overlayBlocks.computeIfAbsent(block, k -> k instanceof TransparentBlock || k instanceof LeavesBlock);
	}

	public void onFluidRendererReload(FluidRenderer renderer, Sprite[] waterSprites, Sprite[] lavaSprites, Sprite waterOverlay) {
		fluidRenderer = renderer;

		Sprite[] waterSpritesFull = {waterSprites[0], waterSprites[1], waterOverlay};
		ExpandedFluidRenderHandler waterHandler = new ExpandedFluidRenderHandler() {
			@Override
			public Sprite[] getFluidSprites(BlockRenderView view, BlockPos pos, FluidState state) {
				return waterSpritesFull;
			}

			@Override
			public int getFluidColor(BlockRenderView view, BlockPos pos, FluidState state) {
				if (view != null && pos != null) {
					return BiomeColors.getWaterColor(view, pos);
				} else {
					return DEFAULT_WATER_COLOR;
				}
			}
		};

		//noinspection Convert2Lambda
		ExpandedFluidRenderHandler lavaHandler = new ExpandedFluidRenderHandler() {
			@Override
			public Sprite[] getFluidSprites(BlockRenderView view, BlockPos pos, FluidState state) {
				return lavaSprites;
			}
		};

		register(Fluids.WATER, waterHandler);
		register(Fluids.FLOWING_WATER, waterHandler);
		register(Fluids.LAVA, lavaHandler);
		register(Fluids.FLOWING_LAVA, lavaHandler);
		handlers.putAll(modHandlers);

		SpriteAtlasTexture texture = MinecraftClient.getInstance()
				.getBakedModelManager()
				.getAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

		for (ExpandedFluidRenderHandler handler : handlers.values()) {
			handler.reloadTextures(texture);
		}
	}

	public void renderFluid(BlockPos pos, BlockRenderView world, VertexConsumer vertexConsumer, BlockState state, FluidState fluidState) {
		fluidRenderer.render(world, pos, vertexConsumer, state, fluidState);
	}

}