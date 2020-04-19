package io.github.vampirestudios.obsidian.api;

import io.github.vampirestudios.obsidian.configPack.ConfigPackInfo;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;

public interface IAddonPack {

    ConfigPackInfo getConfigPackInfo();

    Identifier getIdentifier();

    String getDisplayName();

    ResourcePack getVirtualResourcePack();

}