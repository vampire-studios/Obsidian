package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.swordglowsblue.artifice.api.Artifice;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.text.WordUtils;

import java.io.IOException;

public class BlockInitThread implements Runnable {

	private final Block block;

	public BlockInitThread(Block blockIn) {
		block = blockIn;
	}

	@Override
	public void run() {
		try {
			net.minecraft.block.Block block1 = Registry.BLOCK.get(block.information.name.id);
			BlockRenderLayerMap.INSTANCE.putBlock(block1, block.information.translucent ? RenderLayer.getTranslucent() : RenderLayer.getCutoutMipped());
			Artifice.registerAssetPack(String.format("%s:%s_assets", block.information.name.id.getNamespace(), block.information.name.id.getPath()), clientResourcePackBuilder -> {
				if (block.information.name.translated != null) {
					block.information.name.translated.forEach((languageId, name) -> {
						String blockName;
						if (name.contains("_")) {
							blockName = WordUtils.capitalizeFully(name.replace("_", " "));
						} else {
							blockName = name;
						}
						clientResourcePackBuilder.addTranslations(new Identifier(block.information.name.id.getNamespace(), languageId), translationBuilder ->
								translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath()), blockName));
					});
				}
				if (block.display != null) {
					if (block.display.lore.length != 0) {
						for (TooltipInformation lore : block.display.lore) {
							if (lore.text.textType.equals("translatable")) {
								lore.text.translated.forEach((languageId, name) ->
										clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
												translationBuilder.entry(lore.text.text, name)));
							}
						}
					}
					if (block.display.model != null) {
						if (block.additional_information != null) {
							if (block.additional_information.horizontal_rotatable) {
								clientResourcePackBuilder.addBlockState(block.information.name.id, blockStateBuilder -> {
									blockStateBuilder.variant("facing=north", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/")));
									blockStateBuilder.variant("facing=south", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/")).rotationY(180));
									blockStateBuilder.variant("facing=east", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/")).rotationY(90));
									blockStateBuilder.variant("facing=west", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/")).rotationY(270));
								});
								clientResourcePackBuilder.addBlockModel(block.information.name.id, modelBuilder -> {
									modelBuilder.parent(block.display.model.parent);
									block.display.model.textures.forEach(modelBuilder::texture);
								});
								clientResourcePackBuilder.addItemModel(block.information.name.id, modelBuilder ->
										modelBuilder.parent(Utils.prependToPath(block.information.name.id, "block/")));
							} else if (block.additional_information.rotatable) {
								clientResourcePackBuilder.addBlockState(block.information.name.id, blockStateBuilder -> {
									blockStateBuilder.variant("facing=north", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/")));
									blockStateBuilder.variant("facing=south", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/")).rotationY(180));
									blockStateBuilder.variant("facing=east", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/")).rotationY(90));
									blockStateBuilder.variant("facing=west", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/")).rotationY(270));
								});
								clientResourcePackBuilder.addBlockModel(block.information.name.id, modelBuilder -> {
									modelBuilder.parent(block.display.model.parent);
									block.display.model.textures.forEach(modelBuilder::texture);
								});
								clientResourcePackBuilder.addItemModel(block.information.name.id, modelBuilder ->
										modelBuilder.parent(Utils.prependToPath(block.information.name.id, "block/")));
							} else if (block.additional_information.pillar) {
								clientResourcePackBuilder.addBlockState(block.information.name.id, blockStateBuilder -> {
									blockStateBuilder.variant("axis=x", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/"))
											.rotationX(90).rotationY(90));
									blockStateBuilder.variant("axis=y", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/")));
									blockStateBuilder.variant("axis=z", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/"))
											.rotationX(90));
								});
								clientResourcePackBuilder.addBlockModel(block.information.name.id, modelBuilder -> {
									modelBuilder.parent(block.display.model.parent);
									block.display.model.textures.forEach(modelBuilder::texture);
								});
								clientResourcePackBuilder.addItemModel(block.information.name.id, modelBuilder ->
										modelBuilder.parent(Utils.prependToPath(block.information.name.id, "block/")));
							} else {
								clientResourcePackBuilder.addBlockState(block.information.name.id, blockStateBuilder ->
										blockStateBuilder.variant("", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/"))));
								clientResourcePackBuilder.addBlockModel(block.information.name.id, modelBuilder -> {
									modelBuilder.parent(block.display.model.parent);
									block.display.model.textures.forEach(modelBuilder::texture);
								});
								clientResourcePackBuilder.addItemModel(block.information.name.id, modelBuilder -> {
									modelBuilder.parent(Utils.prependToPath(block.information.name.id, "block/"));
								});
							}
						} else {
							clientResourcePackBuilder.addBlockState(block.information.name.id, blockStateBuilder ->
									blockStateBuilder.variant("", variant -> variant.model(Utils.prependToPath(block.information.name.id, "block/"))));
							clientResourcePackBuilder.addBlockModel(block.information.name.id, modelBuilder -> {
								modelBuilder.parent(block.display.model.parent);
								block.display.model.textures.forEach(modelBuilder::texture);
							});
							clientResourcePackBuilder.addItemModel(block.information.name.id, modelBuilder ->
									modelBuilder.parent(Utils.prependToPath(block.information.name.id, "block/")));
						}
					}
				}
				try {
					if (FabricLoader.getInstance().isDevelopmentEnvironment())
						clientResourcePackBuilder.dumpResources("testing", "assets");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			if (block.additional_information != null) {
				if (block.additional_information.stairs) {
					Artifice.registerAssetPack(String.format("%s:%s_stairs_assets", block.information.name.id.getNamespace(), block.information.name.id.getPath()), clientResourcePackBuilder -> {
						if (block.information.name.translated != null) {
							block.information.name.translated.forEach((languageId, name) ->
									clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
											translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_stairs"),
													name + " Stairs")));
						}
						try {
							if (FabricLoader.getInstance().isDevelopmentEnvironment())
								clientResourcePackBuilder.dumpResources("testing", "assets");
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
				}
				if (block.additional_information.fence) {
					Artifice.registerAssetPack(String.format("%s:%s_fence_assets", block.information.name.id.getNamespace(), block.information.name.id.getPath()), clientResourcePackBuilder -> {
						if (block.information.name.translated != null) {
							block.information.name.translated.forEach((languageId, name) ->
									clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
											translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_fence"),
													name + " Fence")));
						}
						try {
							if (FabricLoader.getInstance().isDevelopmentEnvironment())
								clientResourcePackBuilder.dumpResources("testing", "assets");
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
				}
				if (block.additional_information.fenceGate) {
					Artifice.registerAssetPack(String.format("%s:%s_fence_gate_assets", block.information.name.id.getNamespace(), block.information.name.id.getPath()), clientResourcePackBuilder -> {
						if (block.information.name.translated != null) {
							block.information.name.translated.forEach((languageId, name) ->
									clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
											translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_fence_gate"),
													name + " Fence Gate")));
						}
						try {
							if (FabricLoader.getInstance().isDevelopmentEnvironment())
								clientResourcePackBuilder.dumpResources("testing", "assets");
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
				}
				if (block.additional_information.walls) {
					Artifice.registerAssetPack(String.format("%s:%s_wall_assets", block.information.name.id.getNamespace(), block.information.name.id.getPath()), clientResourcePackBuilder -> {
						if (block.information.name.translated != null) {
							block.information.name.translated.forEach((languageId, name) ->
									clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
											translationBuilder.entry(String.format("block.%s.%s", block.information.name.id.getNamespace(), block.information.name.id.getPath() + "_wall"),
													name + " Wall")));
						}
						try {
							if (FabricLoader.getInstance().isDevelopmentEnvironment())
								clientResourcePackBuilder.dumpResources("testing", "assets");
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
