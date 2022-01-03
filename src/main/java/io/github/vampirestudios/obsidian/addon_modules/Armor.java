package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.item.ArmorMaterial;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ArmorItemImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.CustomArmorMaterial;
import io.github.vampirestudios.obsidian.minecraft.obsidian.DyeableArmorItemImpl;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import io.github.vampirestudios.obsidian.utils.RegistryUtils;
import net.minecraft.item.Item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Armor implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem armor = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem.class);
        try {
            if (armor == null) return;
            ArmorMaterial material;
            if (armor.armorMaterial != null && ARMOR_MATERIALS.containsId(armor.armorMaterial)) {
                material = ARMOR_MATERIALS.get(armor.armorMaterial);
            } else {
                material = armor.material;
            }
            CustomArmorMaterial customArmorMaterial = new CustomArmorMaterial(material);

            Item item;
            Item.Settings settings = new Item.Settings()
                    .group(armor.information.getItemGroup()).maxCount(armor.information.max_count)
                    .rarity(armor.information.getRarity());
            if (armor.information.dyeable) item = new DyeableArmorItemImpl(customArmorMaterial, armor, settings);
            else item = new ArmorItemImpl(customArmorMaterial, armor, settings);
            RegistryUtils.registerItem(item, armor.information.name.id);

            register(ARMORS, "armor", armor.information.name.id, armor);
        } catch (Exception e) {
            failedRegistering("armor", armor.information.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "items/armor";
    }
}
