package io.github.vampirestudios.obsidian.client.resource;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.pack.ResourcePackProfile;
import net.minecraft.resource.pack.ResourcePackProvider;

import java.util.function.Consumer;

public class ObsidianAddonResourcePackProvider implements ResourcePackProvider {
    protected SPResourcePack virtualPack;
    private final IAddonPack addonPack;

    public ObsidianAddonResourcePackProvider(IAddonPack addonPack) {
        this.addonPack = addonPack;
        this.virtualPack = new SPResourcePack(addonPack.getConfigPackInfo().folderName, ResourceType.CLIENT_RESOURCES,
                addonPack.getFile().toPath());
    }

    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder, ResourcePackProfile.Factory factory) {
        ResourcePackProfile profile = ResourcePackProfile.of(
                addonPack.getConfigPackInfo().namespace,
                true,
                () -> virtualPack,
                factory,
                ResourcePackProfile.InsertionPosition.BOTTOM,
                Obsidian.RESOURCE_PACK_SOURCE
        );

        Obsidian.LOGGER.info("Registering client-side SpoornPacks ResourcePackProfile={}", profile);

        assert profile != null;
        profileAdder.accept(new SPResourcePackProfile(profile));
    }

}