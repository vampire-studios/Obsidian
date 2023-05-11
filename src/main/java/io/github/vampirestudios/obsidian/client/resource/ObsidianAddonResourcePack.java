package io.github.vampirestudios.obsidian.client.resource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.configPack.LegacyObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;

import java.io.InputStream;
import java.util.Set;

public class ObsidianAddonResourcePack implements PackResources {
    private final PackResources virtualPack;
    private final IAddonPack addonPack;

    public ObsidianAddonResourcePack(IAddonPack addonPack) {
        this.addonPack = addonPack;
        this.virtualPack = addonPack.getVirtualResourcePack();
    }

    @Override
    public IoSupplier<InputStream> getRootResource(String... var1) {
//        try {
//            return virtualPack.getRootResource(var1);
//        } catch (Throwable ignored) {}
        return null;
    }

    @Override
    public IoSupplier<InputStream> getResource(PackType var1, ResourceLocation var2) {
//        try {
//            return virtualPack.getResource(var1, var2);
//        } catch (Throwable ignored) {}
        return null;
    }

    @Override
    public void listResources(PackType resourceType, String string, String string2, ResourceOutput resultConsumer) {
//        virtualPack.listResources(resourceType, string, string2, resultConsumer);
    }

    @Override
    public Set<String> getNamespaces(PackType var1) {
        return Set.of(/*virtualPack.getNamespaces(var1)*/"minecraft", "tutorial");
    }

    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> metadataReader) {
        JsonObject object = new JsonObject();
        if (metadataReader.getMetadataSectionName().equals("pack")) {
            object.addProperty("description", "Default pack for config packs.");
            object.addProperty("pack_format", 8);
        }
        if (metadataReader.getMetadataSectionName().equals("filter")) {
            object.add("block", new JsonArray());
        }
        return metadataReader.fromJson(object);
    }

    @Override
    public String packId() {
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
//        try {
//            virtualPack.close();
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
    }
}