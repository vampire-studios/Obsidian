package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.configPack.BaseAddonInfo;
import net.minecraft.resource.pack.ResourcePack;

import java.io.File;

public interface IAddonPack {

    File getFile();

    BaseAddonInfo getConfigPackInfo();

    String getObsidianDisplayName();

    ResourcePack getVirtualResourcePack();

}