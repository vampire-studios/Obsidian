package io.github.vampirestudios.obsidian.api.bedrock;

import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;

public interface IBedrockAddon {

	ManifestFile getManifestFile();

	Identifier getIdentifier();

	String getDisplayName();

	ResourcePack getVirtualResourcePack();

}