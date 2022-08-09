package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.statusEffects.StatusEffect;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.minecraft.obsidian.StatusEffectImpl;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class StatusEffects implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        StatusEffect statusEffect = Obsidian.GSON.fromJson(new FileReader(file), StatusEffect.class);
        try {
            if (statusEffect == null) return;
            String color1 = statusEffect.color.replace("#", "").replace("0x", "");
            Registry.register(Registry.STATUS_EFFECT, statusEffect.name.id, new StatusEffectImpl(statusEffect,
                    Integer.parseInt(color1, 16)));
            register(ContentRegistries.STATUS_EFFECTS, "status_effect", statusEffect.name.id, statusEffect);
        } catch (Exception e) {
            failedRegistering("status_effect", statusEffect.name.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "status_effects";
    }
}
