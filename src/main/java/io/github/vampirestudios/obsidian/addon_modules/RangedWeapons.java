package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelperItemExpanded;
import io.github.vampirestudios.obsidian.api.obsidian.item.RangedWeaponItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.BowItemImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.CrossbowItemImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.TridentItemImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class RangedWeapons implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        RangedWeaponItem rangedWeapon = BaseGson.GSON.fromJson(new FileReader(file), RangedWeaponItem.class);
        try {
            if (rangedWeapon == null) return;
            Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""));
            rangedWeapon.information.id = identifier;
            RegistryHelperItemExpanded expanded = new RegistryHelperItemExpanded(id.modId());

            Item.Properties settings = ItemModuleHelper.baseProperties(rangedWeapon).setId(ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, identifier));
            var creativeTab = ItemModuleHelper.getCreativeTab(rangedWeapon);

            switch (rangedWeapon.weapon_type) {
                case BOW      -> expanded.registerItem(identifier.getPath(), new BowItemImpl(rangedWeapon, settings), creativeTab);
                case CROSSBOW -> expanded.registerItem(identifier.getPath(), new CrossbowItemImpl(rangedWeapon, settings), creativeTab);
                case TRIDENT  -> expanded.registerItem(identifier.getPath(), new TridentItemImpl(rangedWeapon, settings), creativeTab);
                case null     -> failedRegistering("ranged_weapon", file.getName(), new IllegalArgumentException("weapon_type must be specified"));
            }
            register(ContentRegistries.RANGED_WEAPONS, "ranged_weapon", identifier, rangedWeapon);
        } catch (Exception e) {
            failedRegistering("ranged_weapon", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "item/weapon/ranged";
    }
}
