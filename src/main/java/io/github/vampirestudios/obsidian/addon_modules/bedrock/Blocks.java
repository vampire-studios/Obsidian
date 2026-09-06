package io.github.vampirestudios.obsidian.addon_modules.bedrock;

import io.github.vampirestudios.obsidian.api.bedrock.block.BaseBlock;
import io.github.vampirestudios.obsidian.api.bedrock.block.Component;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelperBlockExpanded;
import io.github.vampirestudios.obsidian.minecraft.bedrock.BlockImpl;
import io.github.vampirestudios.obsidian.registry.BedrockContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.io.File;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.BedrockAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.BedrockAddonLoader.register;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.REGISTRY_HELPER;

public class Blocks implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		BaseBlock baseBlock = AddonFormats.read(addon, file, BaseBlock.class);
		try {
			if (baseBlock == null) return;

			BlockBehaviour.Properties blockSettings;

			Component component = baseBlock.block.components;

			if (component != null) {
				blockSettings = BlockBehaviour.Properties.of()
						.strength(component.destroy_time, component.explosion_resistance)
						.lightLevel(state -> component.light_emission)
						.friction(component.friction);
			} else {
				blockSettings = BlockBehaviour.Properties.of();
			}

			RegistryHelperBlockExpanded expanded = (RegistryHelperBlockExpanded) REGISTRY_HELPER.blocks();

			Block block = expanded.registerBlock(new BlockImpl(baseBlock, blockSettings),
					baseBlock.block.description.identifier.getPath(), CreativeModeTabs.BUILDING_BLOCKS);

			if (component != null && component.flammable != null) {
				FlammableBlockRegistry.getDefaultInstance().add(block, component.flammable.catch_chance_modifier,
						component.flammable.destroy_chance_modifier);
			}

			register(BedrockContentRegistries.BLOCKS, "block", baseBlock.block.description.identifier, baseBlock);
		} catch (Exception e) {
			failedRegistering("block", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "blocks";
	}

}