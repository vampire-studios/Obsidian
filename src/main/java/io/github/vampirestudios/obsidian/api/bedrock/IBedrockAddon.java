package io.github.vampirestudios.obsidian.api.bedrock;

import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import net.minecraft.resources.ResourceLocation;

public interface IBedrockAddon extends IAddonPack {

    ManifestFile getManifestFile();

    ResourceLocation getIdentifier();

}