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
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Holder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.group.api.QuiltItemGroup;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;

public class CreativeTabs implements AddonModule {

    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        JsonObject jsonObject = Obsidian.GSON.fromJson(new FileReader(file), JsonObject.class);
        Identifier identifier = new Identifier(id.modId(), id.addonPath());

        try {
            DataResult<Pair<CreativeTab, JsonElement>> result = CreativeTab.CODEC.decode(JsonOps.INSTANCE, jsonObject);
            if (result.error().isPresent()) {
                Obsidian.LOGGER.error(String.format("Unable to parse creative tab file %s.\nReason: %s", id, result.error().get().message()));
                return;
            }

            if (result.result().isPresent()) {
                CreativeTab creativeTab = result.result().get().getFirst();
                ItemGroup itemGroup1 = QuiltItemGroup.builder(identifier)
                        .icon(() -> new ItemStack(creativeTab.icon))
                        .appendItems(itemStacks -> {
                            for (Holder<Item> item : creativeTab.items) {
                                if (item.isBound()) {
                                    itemStacks.add(new ItemStack(item.value()));
                                }
                            }
                        })
                        .displayText(Text.translatable(String.format("itemGroup.%s.%s", identifier.getNamespace(), identifier.getPath())))
                        .build();
                creativeTab.texture.ifPresent(value -> itemGroup1.setTexture(value.toString()));
                Registry.register(Registries.ITEM_GROUP_REGISTRY, new Identifier(id.modId(), id.addonPath()), itemGroup1);
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