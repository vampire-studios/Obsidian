package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.configPack.BaseAddonInfo;
import net.minecraft.server.packs.PackResources;
import net.vampirestudios.arrp.api.RuntimeResourcePack;

import java.io.File;

public interface IAddonPack {

    File getFile();

    BaseAddonInfo getConfigPackInfo();

    String getObsidianDisplayName();

    PackResources getVirtualResourcePack();

    RuntimeResourcePack getResourcePack();

}