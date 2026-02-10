package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelperItemExpanded;
import io.github.vampirestudios.obsidian.api.obsidian.item.ShieldItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ShieldItemImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Shields implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        ShieldItem shieldItem = BaseGson.GSON.fromJson(new FileReader(file), ShieldItem.class);
        try {
            if(shieldItem == null) return;

            Identifier identifier;
            if (shieldItem.information.name.id != null) {
                identifier = shieldItem.information.name.id;
            } else {
                identifier = Identifier.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""));
                shieldItem.information.name.id = identifier;
            }

            Item.Properties settings = new Item.Properties();
            settings.stacksTo(shieldItem.information.getItemSettings().maxStackSize);
            settings.rarity(Rarity.valueOf(shieldItem.information.getItemSettings().rarity.toUpperCase(Locale.ROOT)));
            if (shieldItem.information.getItemSettings().durability != 0)
                settings.durability(shieldItem.information.getItemSettings().durability);
            if (shieldItem.information.getItemSettings().fireproof) settings.fireResistant();
            if (shieldItem.information.getItemSettings().isEnchantable)
                settings.enchantable(shieldItem.information.getItemSettings().enchantability);
            settings.setId(ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, identifier));
            if (shieldItem.can_have_banner)
                settings.component(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);

            RegistryHelperItemExpanded expanded = new RegistryHelperItemExpanded(id.modId());

            expanded.registerItem(identifier.getPath(), new ShieldItemImpl(shieldItem, settings),
                    shieldItem.information.getItemSettings().getItemGroup()
            );
            register(ContentRegistries.SHIELDS, "shield", identifier, shieldItem);
        } catch (Exception e) {
            failedRegistering("shield", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "item/shield";
    }
}
