package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
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

public class Tools implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem tool = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem.class);
        try {
            if (tool == null) return;
            CustomToolMaterial material = new CustomToolMaterial(tool.material);
            Item.Properties settings = new Item.Properties().stacksTo(tool.information.maxStackSize)
                    .rarity(Rarity.valueOf(tool.information.rarity.toUpperCase(Locale.ROOT)));
            ResourceLocation identifier = Objects.requireNonNullElseGet(
                    tool.information.name.id,
                    () -> new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (tool.information.name.id == null) tool.information.name.id = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));

            Item item = null;
            switch (tool.tool_type) {
                case "pickaxe" -> item = REGISTRY_HELPER.items().registerItem(identifier.getPath(), new PickaxeItemImpl(tool, material, tool.attackDamage, tool.attackSpeed, settings));
                case "shovel" -> item = REGISTRY_HELPER.items().registerItem(identifier.getPath(), new ShovelItemImpl(tool, material, tool.attackDamage, tool.attackSpeed, settings));
                case "hoe" -> item = REGISTRY_HELPER.items().registerItem(identifier.getPath(), new HoeItemImpl(tool, material, tool.attackDamage, tool.attackSpeed, settings));
                case "axe" -> item = REGISTRY_HELPER.items().registerItem(identifier.getPath(), new AxeItemImpl(tool, material, tool.attackDamage, tool.attackSpeed, settings));
            }
            Item finalItem = item;
            ItemGroupEvents.modifyEntriesEvent(tool.information.getItemGroup()).register(entries -> entries.accept(finalItem));
            register(ContentRegistries.TOOLS, "tool", identifier, tool);
        } catch (Exception e) {
            failedRegistering("tool", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "items/tools";
    }
}
