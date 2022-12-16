package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.potion.Potion;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Potions implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        Potion potion = Obsidian.GSON.fromJson(new FileReader(file), Potion.class);
        try {
            if (potion == null) return;
            Identifier identifier = Objects.requireNonNullElseGet(
                    potion.name,
                    () -> new Identifier(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (potion.name == null) potion.name = new Identifier(id.modId(), file.getName().replaceAll(".json", ""));
            Registry.register(Registries.POTION, identifier, new net.minecraft.potion.Potion(
                    new StatusEffectInstance(potion.getEffectType(), potion.getEffects().duration * 20, potion.getEffects().amplifier)
            ));
            register(ContentRegistries.POTIONS, "potion", identifier, potion);
        } catch (Exception e) {
            failedRegistering("potion", file.getName(), e);
        }
    }

    @Override
    public String getType() {
        return "potions";
    }
}
