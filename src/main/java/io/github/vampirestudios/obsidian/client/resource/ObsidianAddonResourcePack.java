package io.github.vampirestudios.obsidian.client.resource;

import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.pack.ResourcePack;
import net.minecraft.resource.pack.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class ObsidianAddonResourcePack implements ResourcePack {
    private final ResourcePack virtualPack;
    private final IAddonPack addonPack;

    public ObsidianAddonResourcePack(IAddonPack addonPack) {
        this.addonPack = addonPack;
        this.virtualPack = addonPack.getVirtualResourcePack();
    }

    @Override
    public InputStream openRoot(String var1) throws IOException {
        try {
            return virtualPack.openRoot(var1);
        } catch (Throwable ignored) {}
        throw new FileNotFoundException();
    }

    @Override
    public InputStream open(ResourceType var1, Identifier var2) throws IOException {
        try {
            return virtualPack.open(var1, var2);
        } catch (Throwable ignored) {}
        throw new FileNotFoundException();
    }

    @Override
    public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
        return new HashSet<>(virtualPack.findResources(type, namespace, prefix, maxDepth, pathFilter));
    }

    @Override
    public boolean contains(ResourceType var1, Identifier var2) {
        return virtualPack.contains(var1, var2);
    }

    @Override
    public Set<String> getNamespaces(ResourceType var1) {
        return new HashSet<>(virtualPack.getNamespaces(var1));
    }

    @Override
    public <T> T parseMetadata(ResourceMetadataReader<T> metadataReader) {
        JsonObject object = new JsonObject();
        if (metadataReader.getKey().equals("pack")) {
            object.addProperty("description", "Default pack for config packs.");
            object.addProperty("pack_format", 8);
        }
        return metadataReader.fromJson(object);
    }

    @Override
    public String getName() {
        return "obsidian/" + addonPack.getConfigPackInfo().folderName;
    }

    @Override
    public void close() {
        try {
            virtualPack.close();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}