package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.EmojiCode;
import io.github.vampirestudios.obsidian.api.EmojiType;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.emoji.Emoji;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Emojis implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        Emoji emoji = Obsidian.GSON.fromJson(new FileReader(file), Emoji.class);
        try {
            if (emoji == null) return;
            EmojiType.emojiCodes.add(new EmojiCode(emoji.code, emoji.emoji));
            register(EMOJIS, "emoji", emoji.name, emoji);
        } catch (Exception e) {
            failedRegistering("emoji", emoji.name.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "emoji";
    }
}
