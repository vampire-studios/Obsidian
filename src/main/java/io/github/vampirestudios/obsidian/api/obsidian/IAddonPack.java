package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;
import net.minecraft.resource.pack.ResourcePack;

import java.io.File;

public interface IAddonPack {

    File getFile();

    ObsidianAddonInfo getConfigPackInfo();

    String getDisplayNameObsidian();

    ResourcePack getVirtualResourcePack();

}