package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.emoji.Emoji;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Emojis implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        Emoji emoji = Obsidian.GSON.fromJson(new FileReader(file), Emoji.class);
        try {
            if (emoji == null) return;

            Identifier identifier = Objects.requireNonNullElseGet(
                    emoji.name,
                    () -> new Identifier(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (emoji.name == null) emoji.name = new Identifier(id.modId(), file.getName().replaceAll(".json", ""));

//            EmojiType.emojiCodes.add(new EmojiCode(emoji.code, emoji.emoji));
            register(ContentRegistries.EMOJIS, "emoji", identifier, emoji);
        } catch (Exception e) {
            failedRegistering("emoji", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "emoji";
    }
}
