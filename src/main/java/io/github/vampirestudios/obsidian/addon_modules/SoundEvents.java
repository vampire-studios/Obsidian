package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.MusicDisc;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.minecraft.obsidian.MusicDiscItemImpl;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class SoundEvents implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        MusicDisc musicDisc = Obsidian.GSON.fromJson(new FileReader(file), MusicDisc.class);
        try {
            if (musicDisc == null) return;
            Item.Settings settings = new Item.Settings().group(musicDisc.information.getItemGroup())
                    .maxCount(musicDisc.information.maxStackSize).rarity(musicDisc.information.rarity);
            Registry.register(Registry.ITEM, musicDisc.information.name.id, new MusicDiscItemImpl(musicDisc, settings
                    .maxDamage(musicDisc.information.useDuration)));
            register(ContentRegistries.MUSIC_DISCS, "music_disc", musicDisc.information.name.id, musicDisc);
        } catch (Exception e) {
            failedRegistering("music_disc", musicDisc.information.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "sound_events";
    }
}
