package io.github.vampirestudios.obsidian.threadhandlers.assets_temp;

import io.github.vampirestudios.obsidian.api.SubItemGroup;
import io.github.vampirestudios.obsidian.client.ClientInit;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTab;

public class SubItemGroupInitThread implements Runnable {
    private final SubItemGroup itemGroup;

    public SubItemGroupInitThread(SubItemGroup itemGroup_in) {
        itemGroup = itemGroup_in;
    }

    @Override
    public void run() {
        CreativeModeTab tab = BuiltInRegistries.CREATIVE_MODE_TAB.getValue(itemGroup.targetGroup);
        assert tab != null;
        if (itemGroup.name != null && itemGroup.name.translations != null) {
            itemGroup.name.translations.forEach((languageId, name) -> ClientInit.addTranslation(
                    itemGroup.id.getNamespace(), languageId,
					STR."\{tab.getDisplayName().getString()}.\{itemGroup.id.getPath()}", name
            ));
        }
    }
}
