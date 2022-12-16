package io.github.vampirestudios.obsidian.configPack;

import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ZipResourcePack;

import java.io.File;

public class ObsidianAddon implements IAddonPack {

    private final ObsidianAddonInfo obsidianAddonInfo;
    private final File file;

    public ObsidianAddon(ObsidianAddonInfo obsidianAddonInfo) {
        this(obsidianAddonInfo, null);
    }

    public ObsidianAddon(ObsidianAddonInfo obsidianAddonInfo, File file) {
        this.obsidianAddonInfo = obsidianAddonInfo;
        this.file = file;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public ObsidianAddonInfo getConfigPackInfo() {
        return obsidianAddonInfo;
    }

    @Override
    public String getObsidianDisplayName() {
        return obsidianAddonInfo.addon.name;
    }

    @Override
    public ResourcePack getVirtualResourcePack() {
        if (file == null) return null;
        if (file.isDirectory()) return new DirectoryResourcePack(obsidianAddonInfo.addon.folderName, file.toPath(), false);
        return new ZipResourcePack(obsidianAddonInfo.addon.folderName, file, false);
    }

}