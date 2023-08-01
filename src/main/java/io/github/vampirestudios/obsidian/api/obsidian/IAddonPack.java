package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.configPack.BaseAddonInfo;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.minecraft.server.packs.PackResources;

import java.io.File;

public interface IAddonPack {

    File getFile();

    BaseAddonInfo getConfigPackInfo();

    String getObsidianDisplayName();

    PackResources getVirtualResourcePack();

    RuntimeResourcePack getResourcePack();

}