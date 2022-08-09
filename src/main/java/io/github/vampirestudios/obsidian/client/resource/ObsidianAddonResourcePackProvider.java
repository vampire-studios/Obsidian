package io.github.vampirestudios.obsidian.client.resource;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.configPack.LegacyObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.pack.ResourcePackProfile;
import net.minecraft.resource.pack.ResourcePackProvider;

import java.util.function.Consumer;

public class ObsidianAddonResourcePackProvider implements ResourcePackProvider {
    protected SPResourcePack virtualPack;
    private final IAddonPack addonPack;

    public ObsidianAddonResourcePackProvider(IAddonPack addonPack) {
        this.addonPack = addonPack;
        String folderName;
        if (addonPack.getConfigPackInfo() instanceof LegacyObsidianAddonInfo legacyObsidianAddonInfo) {
            folderName = legacyObsidianAddonInfo.folderName;
        } else {
            ObsidianAddonInfo addonInfo = (ObsidianAddonInfo) addonPack.getConfigPackInfo();
            folderName = addonInfo.addon.folderName;
        }
        this.virtualPack = new SPResourcePack(folderName, ResourceType.CLIENT_RESOURCES,
                addonPack.getFile().toPath());
    }

    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder, ResourcePackProfile.Factory factory) {
        String id;
        if (addonPack.getConfigPackInfo() instanceof LegacyObsidianAddonInfo legacyObsidianAddonInfo) {
            id = legacyObsidianAddonInfo.namespace;
        } else {
            ObsidianAddonInfo addonInfo = (ObsidianAddonInfo) addonPack.getConfigPackInfo();
            id = addonInfo.addon.id;
        }
        ResourcePackProfile profile = ResourcePackProfile.of(
                id,
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