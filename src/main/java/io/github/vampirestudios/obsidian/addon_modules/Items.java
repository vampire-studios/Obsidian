package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.toml.TomlFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.cottonmc.jankson.JanksonFactory;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelperItemExpanded;
import io.github.vampirestudios.obsidian.api.obsidian.RenderModeModel;
import io.github.vampirestudios.obsidian.client.ClientInit;
import io.github.vampirestudios.obsidian.configPack.LegacyObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.hjson.JsonValue;
import org.hjson.Stringify;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Items implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        io.github.vampirestudios.obsidian.api.obsidian.item.Item item;

        if (addon.getConfigPackInfo() instanceof LegacyObsidianAddonInfo) {
            item = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.Item.class);
        } else {
            ObsidianAddonInfo addonInfo = (ObsidianAddonInfo) addon.getConfigPackInfo();
            if (addonInfo.format == ObsidianAddonInfo.Format.JSON) {
                item = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.Item.class);
            } else if (addonInfo.format == ObsidianAddonInfo.Format.JSON5) {
                JsonObject jsonObject = JanksonFactory.builder().build().load(file);
                item = JanksonFactory.builder().build().fromJson(jsonObject, io.github.vampirestudios.obsidian.api.obsidian.item.Item.class);
            } else if (addonInfo.format == ObsidianAddonInfo.Format.YAML) {
                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                mapper.findAndRegisterModules();
                item = mapper.readValue(file, io.github.vampirestudios.obsidian.api.obsidian.item.Item.class);
            } else if (addonInfo.format == ObsidianAddonInfo.Format.TOML) {
                ObjectMapper mapper = new ObjectMapper(new TomlFactory());
                mapper.findAndRegisterModules();
                item = mapper.readValue(file, io.github.vampirestudios.obsidian.api.obsidian.item.Item.class);
            } else if (addonInfo.format == ObsidianAddonInfo.Format.HJSON) {
                item = Obsidian.GSON.fromJson(JsonValue.readHjson(new FileReader(file)).toString(Stringify.FORMATTED), io.github.vampirestudios.obsidian.api.obsidian.item.Item.class);
            } else {
                item = null;
            }
        }
        try {
            if (item == null) return;

            ResourceLocation identifier;
            if (item.information.name.id != null) {
                identifier = item.information.name.id;
            } else {
                identifier = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));
                item.information.name.id = identifier;
            }

            FabricItemSettings settings = new FabricItemSettings();
            settings.stacksTo(item.information.getItemSettings().maxStackSize);
            settings.rarity(Rarity.valueOf(item.information.getItemSettings().rarity.toUpperCase(Locale.ROOT)));
            if (item.information.getItemSettings().durability != 0)
                settings.durability(item.information.getItemSettings().durability);
            if (item.information.getItemSettings().fireproof) settings.fireResistant();

            RegistryHelperItemExpanded expanded = new RegistryHelperItemExpanded(id.modId());

            Item item1;
            if (item.information.getItemSettings().canPlaceBlock) {
                item1 = expanded.registerItem(identifier.getPath(), new BlockItemImpl(item, BuiltInRegistries.BLOCK.get(item.information.getItemSettings().placableBlock), settings),
                        item.information.getItemSettings().getItemGroup());
            } else {
                if (item.information.getItemSettings().renderModeModels != null) {
                    for (RenderModeModel renderModeModel : item.information.getItemSettings().renderModeModels) {
                        ClientInit.customModels.add(new ModelResourceLocation(renderModeModel.model, "inventory"));
                    }
                }
                if (item.information.getItemSettings().wearable && item.information.getItemSettings().maxStackSize <= 1) {
                    settings.equipmentSlot(stack -> EquipmentSlot.byName(item.information.getItemSettings().wearableSlot));
                    if (item.information.getItemSettings().dyeable) {
                        item1 = expanded.registerDyeableItem(new WearableAndDyeableItemImpl(item, settings), identifier.getPath(), item.information.getItemSettings().getItemGroup());
                    } else {
                        item1 = expanded.registerItem(identifier.getPath(), new WearableItemImpl(item, settings), item.information.getItemSettings().getItemGroup());
                    }
                } else {
                    if (item.information.getItemSettings().dyeable) {
                        item1 = expanded.registerDyeableItem(new DyeableItemImpl(item, settings), identifier.getPath(), item.information.getItemSettings().getItemGroup());
                    } else {
                        item1 = expanded.registerItem(identifier.getPath(), new ItemImpl(item, settings), item.information.getItemSettings().getItemGroup());
                    }
                }
            }
            if (item.information.getItemSettings().isFuel) FuelRegistry.INSTANCE.add(item1, item.information.getItemSettings().fuelDuration);
            register(ContentRegistries.ITEMS, "item", identifier, item);
        } catch (Exception e) {
            failedRegistering("item", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "items";
    }
}
