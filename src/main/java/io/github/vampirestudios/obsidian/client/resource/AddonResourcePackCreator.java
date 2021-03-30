package io.github.vampirestudios.obsidian.client.resource;

import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;

import java.util.function.Consumer;

public class AddonResourcePackCreator implements ResourcePackProvider {

    @Override
    public void register(Consumer<ResourcePackProfile> consumer, ResourcePackProfile.Factory factory) {
        ObsidianAddonResourcePack obsidianAddonResourcePack = new ObsidianAddonResourcePack();
        consumer.accept(ResourcePackProfile.of(obsidianAddonResourcePack.getName(), true, () -> obsidianAddonResourcePack, factory, ResourcePackProfile.InsertionPosition.BOTTOM, ResourcePackSource.nameAndSource("pack.source.obsidian")));
    }

}