package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.MaceWeaponImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.MeleeWeaponImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.SpearItemImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.OItemComponents;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Weapons implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        WeaponItem weapon = BaseGson.GSON.fromJson(new FileReader(file), WeaponItem.class);
        try {
            if (weapon == null) return;

            Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""));
            weapon.information.id = identifier;

            Item.Properties settings = createItemProperties(weapon).setId(ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, identifier));
            ResourceKey<CreativeModeTab> creativeTab = getCreativeTab(weapon);

            Item weaponItem = getWeaponItem(weapon, settings);
            Item registeredItem = REGISTRY_HELPER.items().registerItem(identifier.getPath(), weaponItem);
            ItemGroupEvents.modifyEntriesEvent(creativeTab).register(entries -> entries.accept(registeredItem));
            register(ContentRegistries.WEAPONS, "weapon", identifier, weapon);
        } catch (Exception e) {
            failedRegistering("weapon", file.getName(), e);
        }
    }

    private static @NonNull Item getWeaponItem(WeaponItem weapon, Item.Properties settings) {
        ToolMaterial material = weapon.getTier();
		return switch (weapon.weapon_type) {
			case SPEAR        -> new SpearItemImpl(weapon, material, weapon.attackDamage, weapon.attackSpeed, settings);
			case MACE         -> new MaceWeaponImpl(weapon, material, weapon.attackDamage, weapon.attackSpeed, settings);
			case SWORD, null  -> new MeleeWeaponImpl(weapon, material, weapon.attackDamage, weapon.attackSpeed, settings);
		};
    }

    @SuppressWarnings("unchecked")
    static <T> void applyAllComponents(Item.Properties props, DataComponentPatch map) {
        for (var e : map.entrySet()) {
            var type = (DataComponentType<T>) e.getKey();
            var opt  = (Optional<T>) e.getValue();
            opt.ifPresent(v -> props.component(type, v));
        }
    }

    private Item.Properties createItemProperties(io.github.vampirestudios.obsidian.api.obsidian.item.Item item) {
        Item.Properties props = new Item.Properties();

        var comps = item.components;
        if (comps == null) {
            return props;
        }

        applyAllComponents(props, comps);
        return props;
    }

    private ResourceKey<CreativeModeTab> getCreativeTab(io.github.vampirestudios.obsidian.api.obsidian.item.Item item) {
        ResourceKey<CreativeModeTab> creativeTab;

        // Check for OItemComponents.CREATIVE_TAB first
        if (item.components != null && item.components.get(OItemComponents.CREATIVE_TAB) != null &&
                item.components.get(OItemComponents.CREATIVE_TAB).isPresent()) {
            Identifier tabLocation = (Identifier) Objects.requireNonNull(item.components.get(OItemComponents.CREATIVE_TAB)).orElseThrow();
            creativeTab = ResourceKey.create(Registries.CREATIVE_MODE_TAB, tabLocation);
        } else if (item.information.getItemSettings().getItemGroup() != null) {
            creativeTab = item.information.getItemSettings().getItemGroup();
        } else if (item.information.getItemSettings().getParentSettings().getItemGroup() != null) {
            creativeTab = item.information.getItemSettings().getParentSettings().getItemGroup();
        } else {
            creativeTab = CreativeModeTabs.BUILDING_BLOCKS;
        }
        return creativeTab;
    }

    @Override
    public String getType() {
        return "item/weapon";
    }
}
