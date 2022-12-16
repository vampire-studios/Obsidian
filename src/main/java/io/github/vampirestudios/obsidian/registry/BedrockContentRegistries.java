package io.github.vampirestudios.obsidian.registry;

import io.github.vampirestudios.obsidian.api.bedrock.block.BaseBlock;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;

import static io.github.vampirestudios.obsidian.Obsidian.id;

public class BedrockContentRegistries {
	public static Registry<BaseBlock> BLOCKS = FabricRegistryBuilder.createSimple(BaseBlock.class, id("bedrock_blocks")).buildAndRegister();
}
