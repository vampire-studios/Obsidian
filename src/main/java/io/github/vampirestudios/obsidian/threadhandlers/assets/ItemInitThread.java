package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.google.common.collect.ImmutableMap;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.Item;
import io.github.vampirestudios.obsidian.client.ClientInit;

public class ItemInitThread implements Runnable {

    private final Item item;
    private final ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder;

    public ItemInitThread(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, Item itemIn) {
        item = itemIn;
        this.clientResourcePackBuilder = clientResourcePackBuilder;
    }

    @Override
    public void run() {
        if (item.information.name.translated != null) {
            item.information.name.translated.forEach((languageId, name) -> ClientInit.translationMap.put(
                    item.information.name.id.getNamespace(),
                    ImmutableMap.of(
                            languageId,
                            ImmutableMap.of(
                                    String.format("item.%s.%s", item.information.name.id.getNamespace(), item.information.name.id.getPath()),
                                    name
                            )
                    )
            ));
        }
        if (item.display != null && item.display.model != null) {
            clientResourcePackBuilder.addItemModel(item.information.name.id, modelBuilder -> {
                modelBuilder.parent(item.display.model.parent);
                item.display.model.textures.forEach(modelBuilder::texture);
            });
        }
        if (item.display != null && item.display.lore.length != 0) {
            for (TooltipInformation lore : item.display.lore) {
                if (lore.text.textType.equals("translatable")) {
                    lore.text.translated.forEach((languageId, name) -> ClientInit.translationMap.put(
                            item.information.name.id.getNamespace(),
                            ImmutableMap.of(
                                    languageId,
                                    ImmutableMap.of(
                                            lore.text.text,
                                            name
                                    )
                            )
                    ));
                }
            }
        }
    }
}
