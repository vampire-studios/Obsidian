package io.github.vampirestudios.obsidian.api.obsidian;

import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;
import net.minecraft.resource.ResourcePack;

public interface IAddonPack {

	ObsidianAddonInfo getConfigPackInfo();

	String getDisplayNameObsidian();

	ResourcePack getVirtualResourcePack();

}