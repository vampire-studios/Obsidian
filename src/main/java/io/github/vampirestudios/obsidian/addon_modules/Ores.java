package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelperBlockExpanded;
import io.github.vampirestudios.obsidian.minecraft.obsidian.BlockImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.threadhandlers.data.BlockInitThread;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.feature.PlacedFeature;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Ores implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
		io.github.vampirestudios.obsidian.api.obsidian.block.Block block = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
		try {
			if (block == null) return;

			Identifier blockId;
			if (block.description != null) {
				blockId = block.description.identifier;
			} else {
				if (block.information.name.id != null) {
					blockId = block.information.name.id;
				} else {
					blockId = new Identifier(id.modId(), file.getName().replaceAll(".json", ""));
					block.information.name.id = new Identifier(id.modId(), file.getName().replaceAll(".json", ""));
				}
			}

			FabricBlockSettings blockSettings;

			if (block.information.parentBlock != null) {
				blockSettings = FabricBlockSettings.copyOf(net.minecraft.registry.Registries.BLOCK.get(block.information.parentBlock));
			} else {
				blockSettings = FabricBlockSettings.of(block.information.blockProperties.getMaterial());
			}

			blockSettings.hardness(block.information.blockProperties.hardness).resistance(block.information.blockProperties.resistance)
					.sounds(block.information.blockProperties.getBlockSoundGroup())
					.slipperiness(block.information.blockProperties.slipperiness)
					.emissiveLighting((state, world, pos) -> block.information.blockProperties.is_emissive)
					.luminance(block.information.blockProperties.luminance)
					.velocityMultiplier(block.information.blockProperties.velocity_modifier)
					.jumpVelocityMultiplier(block.information.blockProperties.jump_velocity_modifier);
			if (block.information.blockProperties.randomTicks) blockSettings.ticksRandomly();
			if (block.information.blockProperties.instant_break) blockSettings.breakInstantly();
			if (!block.information.blockProperties.collidable) blockSettings.noCollision();
			if (block.information.blockProperties.translucent) blockSettings.nonOpaque();
			if (block.information.blockProperties.dynamic_boundaries) blockSettings.dynamicBounds();

			Item.Settings settings = new Item.Settings();
			if (block.food_information != null) settings.food(Registries.FOOD_COMPONENTS.get(block.food_information.foodComponent));
			if (block.information.itemProperties.fireproof) settings.fireproof();

			RegistryHelperBlockExpanded expanded = (RegistryHelperBlockExpanded) REGISTRY_HELPER.blocks();

			expanded.registerBlock(new BlockImpl(block, blockSettings), block, blockId.getPath(), settings);

			if (block.ore_information != null) {
				RegistryKey<PlacedFeature> key = RegistryKey.of(RegistryKeys.PLACED_FEATURE, Utils.appendToPath(blockId, "_ore_feature"));
				BiomeModifications.addFeature(block.ore_information.biomeSelector(), GenerationStep.Feature.UNDERGROUND_ORES,
						key);
			}

			if (!addon.getConfigPackInfo().hasData) {
				new BlockInitThread(block);
			}

			register(ContentRegistries.ORES, "ore", blockId, block);
		} catch (Exception e) {
			failedRegistering("ore", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "blocks/ores";
	}

}
