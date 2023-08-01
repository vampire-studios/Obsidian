package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.MusicDisc;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.minecraft.obsidian.MusicDiscItemImpl;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class MusicDiscs implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        MusicDisc musicDisc = Obsidian.GSON.fromJson(new FileReader(file), MusicDisc.class);
        try {
            if (musicDisc == null) return;
            Item.Properties settings = new Item.Properties().stacksTo(musicDisc.information.maxStackSize)
                    .rarity(Rarity.valueOf(musicDisc.information.rarity.toUpperCase(Locale.ROOT)));

            ResourceLocation identifier = Objects.requireNonNullElseGet(
                    musicDisc.information.name.id,
                    () -> new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (musicDisc.information.name.id == null) musicDisc.information.name.id = new ResourceLocation(id.modId(), file.getName()
                    .replaceAll(".json", ""));

            Item item = Registry.register(BuiltInRegistries.ITEM, identifier, new MusicDiscItemImpl(musicDisc, settings
                    .durability(musicDisc.information.durability)));
            ItemGroupEvents.modifyEntriesEvent(musicDisc.information.getItemGroup()).register(entries -> entries.accept(item));
            register(ContentRegistries.MUSIC_DISCS, "music_disc", identifier, musicDisc);
        } catch (Exception e) {
            failedRegistering("music_disc", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "items/music_discs";
    }
}
