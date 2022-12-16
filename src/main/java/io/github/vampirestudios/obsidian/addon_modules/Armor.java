package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.ArmorMaterial;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ArmorItemImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.CustomArmorMaterial;
import io.github.vampirestudios.obsidian.minecraft.obsidian.DyeableArmorItemImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Armor implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        JsonObject jsonObject = Obsidian.JANKSON.load(file);
        io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem armor = Obsidian.JANKSON.fromJson(jsonObject, io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem.class);

        try {
            if (armor == null) return;

            Identifier identifier = Objects.requireNonNullElseGet(
                    armor.information.name.id,
                    () -> new Identifier(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (armor.information.name.id == null) armor.information.name.id = new Identifier(id.modId(), file.getName().replaceAll(".json", ""));

            ArmorMaterial material;
            if (armor.armorMaterial != null && ContentRegistries.ARMOR_MATERIALS.containsId(armor.armorMaterial)) {
                material = ContentRegistries.ARMOR_MATERIALS.get(armor.armorMaterial);
            } else {
                material = armor.material;
            }
            CustomArmorMaterial customArmorMaterial = new CustomArmorMaterial(material);

            Item item;
            Item.Settings settings = new Item.Settings()
                    .maxCount(armor.information.maxStackSize)
                    .rarity(armor.information.rarity);
            if (armor.information.dyeable) item = new DyeableArmorItemImpl(customArmorMaterial, armor, settings);
            else item = new ArmorItemImpl(customArmorMaterial, armor, settings);
            REGISTRY_HELPER.items().registerItem(identifier.getPath(), item);
            ItemGroupEvents.modifyEntriesEvent(armor.information.getItemGroup()).register(entries -> entries.add(item));

            register(ContentRegistries.ARMORS, "armor", identifier, armor);
        } catch (Exception e) {
            failedRegistering("armor", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "items/armor";
    }
}
