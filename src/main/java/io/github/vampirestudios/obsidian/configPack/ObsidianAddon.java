package io.github.vampirestudios.obsidian.configPack;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PathPackResources;

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
    public PackResources getVirtualResourcePack() {
        if (file.getParentFile() == null) return null;
        if (file.getParentFile().isDirectory()) return new PathPackResources(obsidianAddonInfo.addon.folderName, file.getParentFile().toPath(), true);
        else return new FilePackResources(obsidianAddonInfo.addon.folderName, file.getParentFile(), true);
    }

    @Override
    public RuntimeResourcePack getResourcePack() {
        return RuntimeResourcePack.create(Obsidian.id(obsidianAddonInfo.addon.id));
    }

}