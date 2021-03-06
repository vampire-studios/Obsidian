package io.github.vampirestudios.obsidian.threadhandlers.assets;

import com.google.common.collect.ImmutableMap;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import io.github.vampirestudios.obsidian.ArmorRenderingRegistry;
import io.github.vampirestudios.obsidian.api.obsidian.ArmorModel;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ArmorModelImpl;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

public class ArmorInitThread implements Runnable {
    private final ArmorItem armor;
    private final ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder;

    public ArmorInitThread(ArtificeResourcePack.ClientResourcePackBuilder clientResourcePackBuilder, ArmorItem armor_in) {
        armor = armor_in;
        this.clientResourcePackBuilder = clientResourcePackBuilder;
    }

    @Override
    public void run() {
        if (armor.information.name.translated != null)
            armor.information.name.translated.forEach((languageId, name) -> ClientInit.translationMap.put(
                    armor.information.name.id.getNamespace(),
                    ImmutableMap.of(
                            languageId,
                            ImmutableMap.of(
                                    String.format("item.%s.%s", armor.information.name.id.getNamespace(), armor.information.name.id.getPath()),
                                    name
                            )
                    )
            ));

        if (armor.display != null && armor.display.model != null)
            clientResourcePackBuilder.addItemModel(armor.information.name.id, modelBuilder -> {
                modelBuilder.parent(armor.display.model.parent);
                armor.display.model.textures.forEach(modelBuilder::texture);
            });

        if (armor.display != null && armor.display.lore.length != 0) {
            for (TooltipInformation lore : armor.display.lore) {
                if (lore.text.textType.equals("translatable")) {
                    lore.text.translated.forEach((languageId, name) -> ClientInit.translationMap.put(
                            armor.information.name.id.getNamespace(),
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
        ArmorRenderingRegistry.registerModel((entity, stack, slot, defaultModel) -> {
            BipedEntityModel<LivingEntity> entityModel;
            Optional<ArmorModel> model = ObsidianAddonLoader.ARMOR_MODELS.getOrEmpty(armor.material.customArmorModel);
            if(model.isPresent()) {
                entityModel = new ArmorModelImpl<>(model.get());
            } else {
                entityModel = defaultModel;
            }
            return entityModel;
        }, Registry.ITEM.get(armor.information.name.id));
        ArmorRenderingRegistry.registerTexture((entity, stack, slot, secondLayer, suffix, defaultTexture) -> armor.material.texture, Registry.ITEM.get(armor.information.name.id));
    }
}
