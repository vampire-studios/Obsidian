package io.github.vampirestudios.obsidian.configPack;

import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PathPackResources;

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
    public PackResources getVirtualResourcePack() {
        if (file == null) return null;
        if (file.isDirectory()) return new PathPackResources(obsidianAddonInfo.folderName, file.toPath(), false);
        return new FilePackResources(obsidianAddonInfo.folderName, file, false);
    }

    @Override
    public RuntimeResourcePack getResourcePack() {
        return RuntimeResourcePack.create(obsidianAddonInfo.namespace);
    }

}