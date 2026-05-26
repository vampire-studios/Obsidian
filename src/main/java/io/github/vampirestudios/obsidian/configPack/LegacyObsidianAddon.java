package io.github.vampirestudios.obsidian.configPack;

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
    public PackResources getVirtualResourcePack() {
        PackLocationInfo packLocationInfo = new PackLocationInfo(obsidianAddonInfo.folderName, Component.literal(getObsidianDisplayName()),
                PackSource.BUILT_IN, Optional.empty());
        if (file.getParentFile() == null) return null;
        if (file.getParentFile().isDirectory()) return new PathPackResources(packLocationInfo, file.getParentFile().toPath());
        else return new FilePackResources(packLocationInfo, new FilePackResources.SharedZipFileAccess(this.file), obsidianAddonInfo.namespace);
    }

    @Override
    public RuntimeResourcePack getResourcePack() {
        return RuntimeResourcePack.create(obsidianAddonInfo.namespace);
    }

}