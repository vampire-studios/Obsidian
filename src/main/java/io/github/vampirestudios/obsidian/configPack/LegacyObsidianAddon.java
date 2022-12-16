package io.github.vampirestudios.obsidian.configPack;

import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ZipResourcePack;

import java.io.File;

public class LegacyObsidianAddon implements IAddonPack {

    private final LegacyObsidianAddonInfo obsidianAddonInfo;
    private final File file;

    public LegacyObsidianAddon(LegacyObsidianAddonInfo obsidianAddonInfo, File file) {
        this.obsidianAddonInfo = obsidianAddonInfo;
        this.file = file;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public LegacyObsidianAddonInfo getConfigPackInfo() {
        return obsidianAddonInfo;
    }

    @Override
    public String getObsidianDisplayName() {
        return obsidianAddonInfo.displayName;
    }

    @Override
    public ResourcePack getVirtualResourcePack() {
        if (file == null) return null;
        if (file.isDirectory()) return new DirectoryResourcePack(obsidianAddonInfo.folderName, file.toPath(), false);
        return new ZipResourcePack(obsidianAddonInfo.folderName, file, false);
    }

}