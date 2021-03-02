package io.github.vampirestudios.obsidian.addonModules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.CustomToolMaterial;
import io.github.vampirestudios.obsidian.minecraft.obsidian.MeleeWeaponImpl;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import io.github.vampirestudios.obsidian.utils.RegistryUtils;
import net.minecraft.item.Item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ConfigHelper.*;

public class Weapons implements AddonModule {
    @Override
    public void init(File file, ModIdAndAddonPath id) throws FileNotFoundException {
        WeaponItem weapon = Obsidian.GSON.fromJson(new FileReader(file), WeaponItem.class);
        try {
            CustomToolMaterial material = new CustomToolMaterial(weapon.material);
            RegistryUtils.registerItem(new MeleeWeaponImpl(weapon, material, weapon.attackDamage, weapon.attackSpeed, new Item.Settings()
                    .group(weapon.information.getItemGroup())
                    .maxCount(weapon.information.max_count)), weapon.information.name.id);
            register(WEAPONS, "weapon", weapon.information.name.id.toString(), weapon);
        } catch (Exception e) {
            failedRegistering("weapon", weapon.information.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "items/weapons";
    }
}
