package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.MeleeWeaponImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ToolMaterial;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Weapons implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        WeaponItem weapon = BaseGson.GSON.fromJson(new FileReader(file), WeaponItem.class);
        try {
            if (weapon == null) return;

            Identifier identifier = Objects.requireNonNullElseGet(
                    weapon.information.name.id,
                    () -> Identifier.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (weapon.information.name.id == null) weapon.information.name.id = Identifier.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""));

            Item.Properties settings = new Item.Properties().stacksTo(weapon.information.getItemSettings().maxStackSize)
                    .rarity(Rarity.valueOf(weapon.information.getItemSettings().rarity.toUpperCase(Locale.ROOT)))
                    .setId(ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, identifier));
            if (weapon.components != null) {
                for (Map.Entry<DataComponentType<?>, Optional<?>> dataComponentTypeOptionalEntry : weapon.components.entrySet()) {
                    settings.component((DataComponentType)dataComponentTypeOptionalEntry.getKey(), dataComponentTypeOptionalEntry.getValue().orElseThrow());
                }
            }

            ToolMaterial material = weapon.getTier();
            Item registeredItem = REGISTRY_HELPER.items().registerItem(identifier.getPath(), new MeleeWeaponImpl(weapon, material, weapon.attackDamage, weapon.attackSpeed, settings));
            ItemGroupEvents.modifyEntriesEvent(weapon.information.getItemSettings().getItemGroup()).register(entries -> entries.accept(registeredItem));
            register(ContentRegistries.WEAPONS, "weapon", identifier, weapon);
        } catch (Exception e) {
            failedRegistering("weapon", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "item/weapon";
    }
}
