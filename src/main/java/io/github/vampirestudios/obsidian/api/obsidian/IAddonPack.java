package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;
import net.minecraft.resource.ResourcePack;
import net.minecraft.util.Identifier;

public interface IAddonPack {

    ObsidianAddonInfo getConfigPackInfo();

    Identifier getIdentifier();

    String getDisplayName();

    ResourcePack getVirtualResourcePack();

}