package io.github.vampirestudios.obsidian.api.bedrock;

import io.github.vampirestudios.obsidian.configPack.BaseAddonInfo;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PathPackResources;

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
    public ResourceLocation getIdentifier() {
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
    public PackResources getVirtualResourcePack() {
        if (file == null) return null;
        if (file.isDirectory()) return new PathPackResources(manifestFile.header.name, file.toPath(), false);
        return new FilePackResources(manifestFile.header.name, file, false);
    }

    @Override
    public RuntimeResourcePack getResourcePack() {
        return null;
    }

}