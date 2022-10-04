package io.github.vampirestudios.obsidian.api.bedrock;

import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import net.minecraft.util.Identifier;

public interface IBedrockAddon extends IAddonPack {

    ManifestFile getManifestFile();

    Identifier getIdentifier();

}