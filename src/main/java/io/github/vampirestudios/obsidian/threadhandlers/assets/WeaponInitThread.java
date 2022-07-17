package io.github.vampirestudios.obsidian.threadhandlers.assets;

import io.github.vampirestudios.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import io.github.vampirestudios.obsidian.client.ClientInit;

public class WeaponInitThread implements Runnable {

    private final WeaponItem weapon;
    private final ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder;

    public WeaponInitThread(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, WeaponItem weaponIn) {
        weapon = weaponIn;
        this.clientResourcePackBuilder = clientResourcePackBuilder;
    }

    @Override
    public void run() {
        if (weapon.information.name.translated != null) {
            weapon.information.name.translated.forEach((languageId, name) -> ClientInit.addTranslation(
                    weapon.information.name.id.getNamespace(), languageId,
                    "item." + weapon.information.name.id.getNamespace() + "." + weapon.information.name.id.getPath(), name
            ));
        }
        if (weapon.display != null && weapon.display.model != null) {
            clientResourcePackBuilder.addItemModel(weapon.information.name.id, modelBuilder -> {
                modelBuilder.parent(weapon.display.model.parent);
                weapon.display.model.textures.forEach(modelBuilder::texture);
            });
        }
        if (weapon.display != null && weapon.display.lore.length != 0) {
            for (TooltipInformation lore : weapon.display.lore) {
                if (lore.text.textType.equals("translatable")) {
                    lore.text.translated.forEach((languageId, name) -> ClientInit.addTranslation(
                            weapon.information.name.id.getNamespace(), languageId, lore.text.text, name
                    ));
                }
            }
        }
    }
}
