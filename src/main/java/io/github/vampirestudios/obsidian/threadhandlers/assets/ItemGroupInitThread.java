package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.google.common.collect.ImmutableMap;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
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
        itemGroup.name.translated.forEach((languageId, name) -> ClientInit.translationMap.put(
                itemGroup.name.id.getNamespace(),
                ImmutableMap.of(
                        languageId,
                        ImmutableMap.of(
                                String.format("itemGroup.%s.%s", itemGroup.name.id.getNamespace(), itemGroup.name.id.getPath()),
                                name
                        )
                )
        ));
    }
}
