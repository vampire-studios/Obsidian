package io.github.vampirestudios.obsidian.threadhandlers.assets_temp;

import io.github.vampirestudios.obsidian.api.obsidian.ItemGroup;
import io.github.vampirestudios.obsidian.client.ClientInit;

public class ItemGroupInitThread implements Runnable {
    private final ItemGroup itemGroup;

    public ItemGroupInitThread(ItemGroup itemGroup_in) {
        itemGroup = itemGroup_in;
    }

    @Override
    public void run() {
        itemGroup.name.translations.forEach((languageId, name) -> ClientInit.addTranslation(
                itemGroup.id.getNamespace(), languageId,
                STR."itemGroup.\{itemGroup.id.getNamespace()}" + "." + itemGroup.id.getPath(), name
        ));
    }
}
