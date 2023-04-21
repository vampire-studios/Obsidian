package io.github.vampirestudios.obsidian.configPack;

import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import java.io.File;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PathPackResources;

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
    public PackResources getVirtualResourcePack() {
        if (file == null) return null;
        if (file.isDirectory()) return new PathPackResources(obsidianAddonInfo.addon.folderName, file.toPath(), false);
        return new FilePackResources(obsidianAddonInfo.addon.folderName, file, false);
    }

}