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
import io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem;
import io.github.vampirestudios.obsidian.api.obsidian.item.ArmorMaterial;
import io.github.vampirestudios.obsidian.configPack.LegacyObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ArmorItemImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.CustomArmorMaterial;
import io.github.vampirestudios.obsidian.minecraft.obsidian.DyeableArmorItemImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.hjson.JsonValue;
import org.hjson.Stringify;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Armor implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        ArmorItem armor;

        if (addon.getConfigPackInfo() instanceof LegacyObsidianAddonInfo) {
            armor = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem.class);
        } else {
            ObsidianAddonInfo addonInfo = (ObsidianAddonInfo) addon.getConfigPackInfo();
            if (addonInfo.format == ObsidianAddonInfo.Format.JSON) {
                armor = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem.class);
            } else if (addonInfo.format == ObsidianAddonInfo.Format.JSON5) {
                JsonObject jsonObject = JanksonFactory.builder().build().load(file);
                armor = JanksonFactory.builder().build().fromJson(jsonObject, io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem.class);
            } else if (addonInfo.format == ObsidianAddonInfo.Format.YAML) {
                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                mapper.findAndRegisterModules();
                armor = mapper.readValue(file, io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem.class);
            } else if (addonInfo.format == ObsidianAddonInfo.Format.TOML) {
                ObjectMapper mapper = new ObjectMapper(new TomlFactory());
                mapper.findAndRegisterModules();
                armor = mapper.readValue(file, io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem.class);
            } else if (addonInfo.format == ObsidianAddonInfo.Format.HJSON) {
                armor = Obsidian.GSON.fromJson(JsonValue.readHjson(new FileReader(file)).toString(Stringify.FORMATTED), io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem.class);
            } else {
                armor = null;
            }
        }

        try {
            if (armor == null) return;

            ResourceLocation identifier;
            if (armor.information.name.id != null) {
                identifier = armor.information.name.id;
            } else {
                identifier = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));
                armor.information.name.id = identifier;
            }

            ArmorMaterial material;
            if (armor.armorMaterial != null && ContentRegistries.ARMOR_MATERIALS.containsKey(armor.armorMaterial)) {
                material = ContentRegistries.ARMOR_MATERIALS.get(armor.armorMaterial);
            } else {
                material = armor.material;
            }
            CustomArmorMaterial customArmorMaterial = new CustomArmorMaterial(material);

            Item item;
            Item.Properties settings = new Item.Properties()
                    .stacksTo(armor.information.maxStackSize)
                    .rarity(Rarity.valueOf(armor.information.rarity.toUpperCase(Locale.ROOT)));
            if (armor.information.dyeable) item = new DyeableArmorItemImpl(customArmorMaterial, armor, settings);
            else item = new ArmorItemImpl(customArmorMaterial, armor, settings);
            REGISTRY_HELPER.items().registerItem(identifier.getPath(), item);
            ItemGroupEvents.modifyEntriesEvent(armor.information.getItemGroup()).register(entries -> entries.accept(item));

            register(ContentRegistries.ARMORS, "armor", identifier, armor);
        } catch (Exception e) {
            failedRegistering("armor", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "items/armor";
    }
}
