package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.CreativeTab;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;

public class CreativeTabs implements AddonModule {

    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        JsonObject jsonObject = Obsidian.GSON.fromJson(new FileReader(file), JsonObject.class);
        ResourceLocation identifier = new ResourceLocation(id.modId(), id.addonPath());

        try {
            DataResult<Pair<CreativeTab, JsonElement>> result = CreativeTab.CODEC.decode(JsonOps.INSTANCE, jsonObject);
            if (result.error().isPresent()) {
                Obsidian.LOGGER.error(String.format("Unable to parse creative tab file %s.\nReason: %s", id, result.error().get().message()));
                return;
            }

            if (result.result().isPresent()) {
                CreativeTab creativeTab = result.result().get().getFirst();
                CreativeModeTab itemGroup1 = FabricItemGroup.builder()
                        .icon(() -> new ItemStack(creativeTab.icon))
                        .displayItems((featureSet, entries) -> {
                            for (Holder<Item> item : creativeTab.items) {
                                if (item.isBound()) {
                                    entries.accept(new ItemStack(item.value()));
                                }
                            }
                        })
                        .title(Component.translatable(String.format("itemGroup.%s.%s", identifier.getNamespace(), identifier.getPath())))
                        .build();
                creativeTab.texture.ifPresent(itemGroup1::setBackgroundImage);
                Registry.register(Registries.ITEM_GROUP_REGISTRY, new ResourceLocation(id.modId(), id.addonPath()), itemGroup1);
                ObsidianAddonLoader.register(ContentRegistries.CREATIVE_TABS, "creative_tab", identifier, creativeTab);
            }
        } catch (Exception e) {
            failedRegistering("creative_tab", identifier, e);
        }
    }

    @Override
    public String getType() {
        return "creative_tabs";
    }

}