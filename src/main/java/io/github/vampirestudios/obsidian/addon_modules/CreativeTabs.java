package io.github.vampirestudios.obsidian.addon_modules;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.CreativeTab;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Holder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.group.api.QuiltItemGroup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class CreativeTabs implements AddonModule {

    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        JsonObject jsonObject = Obsidian.GSON.fromJson(new FileReader(file), JsonObject.class);
        Identifier identifier = new Identifier(id.modId(), id.path());

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
                Registry.register(Obsidian.ITEM_GROUP_REGISTRY, new Identifier(id.modId(), id.path()), itemGroup1);
                ObsidianAddonLoader.register(ObsidianAddonLoader.CREATIVE_TABS, "creative_tab", identifier, creativeTab);
            }
        } catch (Exception e) {
            ObsidianAddonLoader.failedRegistering("creative_tab", identifier, e);
        }
    }

    @Override
    public String getType() {
        return "creative_tabs";
    }

}