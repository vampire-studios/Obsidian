package io.github.vampirestudios.obsidian.configPack;

import io.github.vampirestudios.obsidian.api.IAddonPack;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.util.Identifier;

import java.io.File;

public class ConfigPack implements IAddonPack {

    private final ConfigPackInfo configPackInfo;
    private final File file;

    public ConfigPack(ConfigPackInfo configPackInfo) {
        this(configPackInfo, null);
    }

    public ConfigPack(ConfigPackInfo configPackInfo, File file) {
        this.configPackInfo = configPackInfo;
        this.file = file;
    }

    @Override
    public ConfigPackInfo getConfigPackInfo() {
        return configPackInfo;
    }

    @Override
    public Identifier getIdentifier() {
        return configPackInfo.id;
    }

    @Override
    public String getDisplayName() {
        return configPackInfo.displayName;
    }

    @Override
    public ResourcePack getVirtualResourcePack() {
		if(file == null) return null;
		if(file.isDirectory()) return new DirectoryResourcePack(file);
		return new ZipResourcePack(file);
    }

}