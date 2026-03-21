package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.toml.TomlFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem;
import io.github.vampirestudios.obsidian.api.obsidian.item.ArmorMaterial;
import io.github.vampirestudios.obsidian.configPack.LegacyObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;
import io.github.vampirestudios.obsidian.minecraft.obsidian.ArmorItemImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.DyeableArmorItemImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import org.hjson.JsonValue;
import org.hjson.Stringify;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Armor implements AddonModule {
    ResourceKey<? extends Registry<EquipmentAsset>> ROOT_ID = ResourceKey.createRegistryKey(Obsidian.id("equipment_asset"));


    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        ArmorItem armor;

        if (addon.getConfigPackInfo() instanceof LegacyObsidianAddonInfo) {
            armor = BaseGson.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem.class);
        } else {
            ObsidianAddonInfo addonInfo = (ObsidianAddonInfo) addon.getConfigPackInfo();
            if (addonInfo.format == ObsidianAddonInfo.Format.JSON) {
                armor = BaseGson.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem.class);
            } else if (addonInfo.format == ObsidianAddonInfo.Format.JSON5) {
                JsonObject jsonObject = Jankson.builder().build().load(file);
                armor = Jankson.builder().build().fromJson(jsonObject, io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem.class);
            } else if (addonInfo.format == ObsidianAddonInfo.Format.YAML) {
                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                mapper.findAndRegisterModules();
                armor = mapper.readValue(file, io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem.class);
            } else if (addonInfo.format == ObsidianAddonInfo.Format.TOML) {
                ObjectMapper mapper = new ObjectMapper(new TomlFactory());
                mapper.findAndRegisterModules();
                armor = mapper.readValue(file, io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem.class);
            } else if (addonInfo.format == ObsidianAddonInfo.Format.HJSON) {
                armor = BaseGson.GSON.fromJson(JsonValue.readHjson(new FileReader(file)).toString(Stringify.FORMATTED), io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem.class);
            } else {
                armor = null;
            }
        }

        try {
            if (armor == null) return;

            Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""));
            armor.information.id = identifier;

            net.minecraft.world.item.equipment.ArmorMaterial vanillaArmorMaterial;
            if (armor.armorTemplate == ArmorItem.ArmorTemplate.CUSTOM) {
                ArmorMaterial material;
                if (armor.materialId != null && ContentRegistries.ARMOR_MATERIALS.containsKey(armor.materialId)) {
                    material = ContentRegistries.ARMOR_MATERIALS.getValue(armor.materialId);
                } else {
                    material = null;
                }
                assert material != null;
                assert armor.materialId != null;

                ResourceKey<EquipmentAsset> equipmentAsset = ResourceKey.create(ROOT_ID, armor.materialId);
                vanillaArmorMaterial = new net.minecraft.world.item.equipment.ArmorMaterial(
                        material.durability,
                        material.defense,
                        material.enchantability,
                        material.getEquipSound(),
                        material.toughness,
                        material.knockback_resistance,
                        TagKey.create(Registries.ITEM, material.repair_tag),
                        equipmentAsset
                );
            } else {
                vanillaArmorMaterial = armor.getTemplateMaterial();
            }

            Item item;
            Item.Properties settings = new Item.Properties()
                    .stacksTo(armor.information.getItemSettings().maxStackSize)
                    .rarity(Rarity.valueOf(armor.information.getItemSettings().rarity.toUpperCase(Locale.ROOT)));

            if (armor.armor_type == ArmorItem.Type.HUMANOID)
                settings.humanoidArmor(vanillaArmorMaterial, ArmorType.valueOf(armor.slot.toUpperCase(Locale.ROOT)));
            if (armor.armor_type == ArmorItem.Type.WOLF)
                settings.wolfArmor(vanillaArmorMaterial);
            if (armor.armor_type == ArmorItem.Type.HORSE)
                settings.horseArmor(vanillaArmorMaterial);
            if (armor.armor_type == ArmorItem.Type.NAUTILUS)
                settings.nautilusArmor(vanillaArmorMaterial);

            settings.setId(ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, identifier));

            if (armor.information.getItemSettings().dyeable)
                item = new DyeableArmorItemImpl(armor, settings);
            else
                item = new ArmorItemImpl(armor, settings);
            REGISTRY_HELPER.items().registerItem(identifier.getPath(), item);
            CreativeModeTabEvents.modifyOutputEvent(armor.information.getItemSettings().getItemGroup()).register(entries -> entries.accept(item));

            register(ContentRegistries.ARMORS, "armor", identifier, armor);
        } catch (Exception e) {
            failedRegistering("armor", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "item/armor";
    }
}
