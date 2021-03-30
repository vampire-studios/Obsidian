package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem;
import net.minecraft.util.Identifier;

public class ToolInitThread implements Runnable {

    private final ToolItem tool;
    private final ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder;

    public ToolInitThread(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, ToolItem toolIn) {
        tool = toolIn;
        this.clientResourcePackBuilder = clientResourcePackBuilder;
    }

    @Override
    public void run() {
        if (tool.information.name.translated != null) {
            tool.information.name.translated.forEach((languageId, name) ->
                    clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
                            translationBuilder.entry(String.format("item.%s.%s", tool.information.name.id.getNamespace(), tool.information.name.id.getPath()), name)));
        }
        if (tool.display != null && tool.display.model != null) {
            clientResourcePackBuilder.addItemModel(tool.information.name.id, modelBuilder -> {
                modelBuilder.parent(tool.display.model.parent);
                tool.display.model.textures.forEach(modelBuilder::texture);
            });
        }
        if (tool.display != null && tool.display.lore.length != 0) {
            for (TooltipInformation lore : tool.display.lore) {
                if (lore.text.textType.equals("translatable")) {
                    lore.text.translated.forEach((languageId, name) ->
                            clientResourcePackBuilder.addTranslations(new Identifier(Obsidian.MOD_ID, languageId), translationBuilder ->
                                    translationBuilder.entry(lore.text.text, name)));
                }
            }
        }
    }
}
