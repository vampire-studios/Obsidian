package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.configPack.BaseAddonInfo;
import java.io.File;
import net.minecraft.server.packs.PackResources;

public interface IAddonPack {

    File getFile();

    BaseAddonInfo getConfigPackInfo();

    String getObsidianDisplayName();

    PackResources getVirtualResourcePack();

}