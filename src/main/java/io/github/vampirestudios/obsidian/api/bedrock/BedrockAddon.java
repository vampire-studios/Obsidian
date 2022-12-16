package io.github.vampirestudios.obsidian.api.bedrock;

import io.github.vampirestudios.obsidian.configPack.BaseAddonInfo;
import net.minecraft.resource.DirectoryResourcePack;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.util.Identifier;

import java.io.File;

public class BedrockAddon implements IBedrockAddon {

    private final ManifestFile manifestFile;
    private final File file;

    public BedrockAddon(ManifestFile manifestFile) {
        this(manifestFile, null);
    }

    public BedrockAddon(ManifestFile manifestFile, File file) {
        this.manifestFile = manifestFile;
        this.file = file;
    }

    public ManifestFile getManifestFile() {
        return manifestFile;
    }

    @Override
    public Identifier getIdentifier() {
        return manifestFile.header.identifier;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public BaseAddonInfo getConfigPackInfo() {
        return null;
    }

    @Override
    public String getObsidianDisplayName() {
        return manifestFile.header.name;
    }

    @Override
    public ResourcePack getVirtualResourcePack() {
        if (file == null) return null;
        if (file.isDirectory()) return new DirectoryResourcePack(manifestFile.header.name, file.toPath(), false);
        return new ZipResourcePack(manifestFile.header.name, file, false);
    }

}