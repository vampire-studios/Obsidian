package io.github.vampirestudios.obsidian.configPack;

import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.util.Identifier;

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
    public ObsidianAddonInfo getConfigPackInfo() {
        return obsidianAddonInfo;
    }

    @Override
    public Identifier getIdentifier() {
        return obsidianAddonInfo.id;
    }

    @Override
    public String getDisplayName() {
        return obsidianAddonInfo.displayName;
    }

    @Override
    public ResourcePack getVirtualResourcePack() {
		if(file == null) return null;
		if(file.isDirectory()) return new DirectoryResourcePack(file);
		return new ZipResourcePack(file);
    }

}