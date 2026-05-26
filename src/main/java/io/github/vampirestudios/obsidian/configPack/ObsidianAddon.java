package io.github.vampirestudios.obsidian.configPack;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import net.vampirestudios.arrp.api.RuntimeResourcePack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.PackSource;

import java.io.File;
import java.util.Optional;

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
        PackLocationInfo packLocationInfo = new PackLocationInfo(obsidianAddonInfo.addon.folderName, Component.literal(getObsidianDisplayName()),
                PackSource.BUILT_IN, Optional.empty());
        if (file.getParentFile() == null) return null;
        if (file.getParentFile().isDirectory()) return new PathPackResources(packLocationInfo, file.getParentFile().toPath());
        else return new FilePackResources(packLocationInfo, new FilePackResources.SharedZipFileAccess(this.file), obsidianAddonInfo.addon.id);
    }

    @Override
    public RuntimeResourcePack getResourcePack() {
        return RuntimeResourcePack.create(Obsidian.id(obsidianAddonInfo.addon.id));
    }

}