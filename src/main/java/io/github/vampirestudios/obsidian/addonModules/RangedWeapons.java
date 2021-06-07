package io.github.vampirestudios.obsidian.addonModules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.SimpleTridentItem;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.item.RangedWeaponItem;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import io.github.vampirestudios.obsidian.utils.RegistryUtils;
import net.fabricmc.fabric.api.item.v1.bow.FabricBowItem;
import net.fabricmc.fabric.api.item.v1.crossbow.SimpleCrossbowItem;
import net.minecraft.item.Item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class RangedWeapons implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        RangedWeaponItem rangedWeapon = Obsidian.GSON.fromJson(new FileReader(file), RangedWeaponItem.class);
        try {
            if (rangedWeapon == null) return;
            Item.Settings settings = new Item.Settings().group(rangedWeapon.information.getItemGroup())
                    .maxCount(rangedWeapon.information.max_count).rarity(rangedWeapon.information.getRarity());
            switch (rangedWeapon.weapon_type) {
                case "bow" -> RegistryUtils.registerItem(new FabricBowItem(settings), rangedWeapon.information.name.id);
                case "crossbow" -> RegistryUtils.registerItem(new SimpleCrossbowItem(settings), rangedWeapon.information.name.id);
                case "trident" -> RegistryUtils.registerItem(new SimpleTridentItem(settings), rangedWeapon.information.name.id);
            }
            register(RANGED_WEAPONS, "ranged_weapon", rangedWeapon.information.name.id.toString(), rangedWeapon);
        } catch (Exception e) {
            failedRegistering("ranged_weapon", rangedWeapon.information.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "items/weapons/ranged";
    }
}
