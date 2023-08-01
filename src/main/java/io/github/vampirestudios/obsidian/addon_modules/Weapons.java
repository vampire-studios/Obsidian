package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.CustomToolMaterial;
import io.github.vampirestudios.obsidian.minecraft.obsidian.MeleeWeaponImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Weapons implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        WeaponItem weapon = Obsidian.GSON.fromJson(new FileReader(file), WeaponItem.class);
        try {
            if (weapon == null) return;
            Item.Properties settings = new Item.Properties().stacksTo(weapon.information.maxStackSize)
                    .rarity(Rarity.valueOf(weapon.information.rarity.toUpperCase(Locale.ROOT)));
            ResourceLocation identifier = Objects.requireNonNullElseGet(
                    weapon.information.name.id,
                    () -> new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (weapon.information.name.id == null) weapon.information.name.id = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));
            CustomToolMaterial material = new CustomToolMaterial(weapon.material);
            Item registeredItem = REGISTRY_HELPER.items().registerItem(identifier.getPath(), new MeleeWeaponImpl(weapon, material, weapon.attackDamage, weapon.attackSpeed, settings));
            ItemGroupEvents.modifyEntriesEvent(weapon.information.getItemGroup()).register(entries -> entries.accept(registeredItem));
            register(ContentRegistries.WEAPONS, "weapon", identifier, weapon);
        } catch (Exception e) {
            failedRegistering("weapon", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "items/weapons";
    }
}
