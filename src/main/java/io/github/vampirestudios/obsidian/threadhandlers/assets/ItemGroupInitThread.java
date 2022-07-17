package io.github.vampirestudios.obsidian.threadhandlers.assets;

import io.github.vampirestudios.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.obsidian.api.obsidian.ItemGroup;
import io.github.vampirestudios.obsidian.client.ClientInit;

public class ItemGroupInitThread implements Runnable {
    private final ItemGroup itemGroup;
    private final ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder;

    public ItemGroupInitThread(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder_in, ItemGroup itemGroup_in) {
        itemGroup = itemGroup_in;
        clientResourcePackBuilder = clientResourcePackBuilder_in;
    }

    @Override
    public void run() {
        itemGroup.name.translated.forEach((languageId, name) -> ClientInit.addTranslation(
                itemGroup.name.id.getNamespace(), languageId,
                "itemGroup." + itemGroup.name.id.getNamespace() + "." + itemGroup.name.id.getPath(), name
        ));
    }
}
