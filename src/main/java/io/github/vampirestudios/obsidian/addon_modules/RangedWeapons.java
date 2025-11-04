package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.RangedWeaponItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.BowItemImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.CrossbowItemImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class RangedWeapons implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        RangedWeaponItem rangedWeapon = BaseGson.GSON.fromJson(new FileReader(file), RangedWeaponItem.class);
        try {
            if (rangedWeapon == null) return;
            if (rangedWeapon.information.name.id == null) rangedWeapon.information.name.id = ResourceLocation.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""));
            ResourceLocation identifier = Objects.requireNonNullElseGet(
                    rangedWeapon.information.name.id,
                    () -> ResourceLocation.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""))
            );

            Item.Properties settings = new Item.Properties().stacksTo(rangedWeapon.information.getItemSettings().maxStackSize)
                    .rarity(Rarity.valueOf(rangedWeapon.information.getItemSettings().rarity.toUpperCase(Locale.ROOT)))
                    .setId(ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, identifier));

            Item item = null;
            switch (rangedWeapon.weapon_type) {
                case "bow" -> {
                    item = REGISTRY_HELPER.items().registerItem(identifier.getPath(), new BowItemImpl(rangedWeapon, settings));
                }
                case "crossbow" ->  {
                    item = REGISTRY_HELPER.items().registerItem(identifier.getPath(), new CrossbowItemImpl(rangedWeapon, settings));
                }
                /*case "trident" -> {
                    Item item = RegistryUtils.registerItem(new TridentItemImpl(rangedWeapon, settings), identifier);
                    FabricModelPredicateProviderRegistry.register(item, new Identifier("throwing"), (stack, world, entity, seed) ->
                            entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);
                }*/
            }
            Item finalItem = item;
            ItemGroupEvents.modifyEntriesEvent(rangedWeapon.information.getItemSettings().getItemGroup()).register(entries -> entries.accept(finalItem));
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
