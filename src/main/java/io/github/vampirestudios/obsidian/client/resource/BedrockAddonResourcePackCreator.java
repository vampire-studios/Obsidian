package io.github.vampirestudios.obsidian.client.resource;

import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;

import java.util.function.Consumer;

public class BedrockAddonResourcePackCreator implements ResourcePackProvider {

    @Override
    public void register(Consumer<ResourcePackProfile> consumer, ResourcePackProfile.Factory factory) {
        BedrockAddonResourcePack bedrockAddonResourcePack = new BedrockAddonResourcePack();
        consumer.accept(ResourcePackProfile.of(bedrockAddonResourcePack.getName(), true, () -> bedrockAddonResourcePack, factory, ResourcePackProfile.InsertionPosition.BOTTOM, ResourcePackSource.method_29486("pack.source.bedrock")));
    }

}