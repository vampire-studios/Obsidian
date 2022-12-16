package io.github.vampirestudios.obsidian.client.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.configPack.LegacyObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public class ObsidianAddonResourcePack implements ResourcePack {
    private final ResourcePack virtualPack;
    private final IAddonPack addonPack;

    public ObsidianAddonResourcePack(IAddonPack addonPack) {
        this.addonPack = addonPack;
        this.virtualPack = addonPack.getVirtualResourcePack();
    }

    @Override
    public InputSupplier<InputStream> openRoot(String... var1) {
        try {
            return virtualPack.openRoot(var1);
        } catch (Throwable ignored) {}
        return null;
    }

    @Override
    public InputSupplier<InputStream> open(ResourceType var1, Identifier var2) {
        try {
            return virtualPack.open(var1, var2);
        } catch (Throwable ignored) {}
        return null;
    }

    @Override
    public void findResources(ResourceType resourceType, String string, String string2, ResultConsumer resultConsumer) {
        virtualPack.findResources(resourceType, string, string2, resultConsumer);
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
        if (metadataReader.getKey().equals("filter")) {
            object.add("block", new JsonArray());
        }
        return metadataReader.fromJson(object);
    }

    @Override
    public String getName() {
        String folderName;
        if (addonPack.getConfigPackInfo() instanceof LegacyObsidianAddonInfo legacyObsidianAddonInfo) {
            folderName = legacyObsidianAddonInfo.folderName;
        } else {
            ObsidianAddonInfo addonInfo = (ObsidianAddonInfo) addonPack.getConfigPackInfo();
            folderName = addonInfo.addon.folderName;
        }
        return "obsidian/" + folderName;
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