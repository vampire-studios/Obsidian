package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.ItemGroup;
import net.minecraft.util.Identifier;

public class ItemGroupInitThread implements Runnable {
    private final ItemGroup itemGroup;
    private final ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder;

    public ItemGroupInitThread(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder_in, ItemGroup itemGroup_in) {
        itemGroup = itemGroup_in;
        clientResourcePackBuilder = clientResourcePackBuilder_in;
    }

    @Override
    public void run() {
        itemGroup.name.translated.forEach((languageId, name) -> clientResourcePackBuilder.addTranslations(
                new Identifier(Obsidian.MOD_ID, languageId), translationBuilder -> translationBuilder.entry(
                        String.format("itemGroup.%s.%s", itemGroup.name.id.getNamespace(),
                                itemGroup.name.id.getPath()), name)));
    }
}
