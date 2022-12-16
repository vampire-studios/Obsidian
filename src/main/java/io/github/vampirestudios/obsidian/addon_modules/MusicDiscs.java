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
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class MusicDiscs implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        MusicDisc musicDisc = Obsidian.GSON.fromJson(new FileReader(file), MusicDisc.class);
        try {
            if (musicDisc == null) return;
            Item.Settings settings = new Item.Settings().maxCount(musicDisc.information.maxStackSize)
                    .rarity(musicDisc.information.rarity);

            Identifier identifier = Objects.requireNonNullElseGet(
                    musicDisc.information.name.id,
                    () -> new Identifier(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (musicDisc.information.name.id == null) musicDisc.information.name.id = new Identifier(id.modId(), file.getName()
                    .replaceAll(".json", ""));

            Item item = Registry.register(Registries.ITEM, identifier, new MusicDiscItemImpl(musicDisc, settings
                    .maxDamage(musicDisc.information.useDuration)));
            ItemGroupEvents.modifyEntriesEvent(musicDisc.information.getItemGroup()).register(entries -> entries.add(item));
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
