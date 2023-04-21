package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.potion.Potion;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Potions implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        Potion potion = Obsidian.GSON.fromJson(new FileReader(file), Potion.class);
        try {
            if (potion == null) return;
            ResourceLocation identifier = Objects.requireNonNullElseGet(
                    potion.name,
                    () -> new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""))
            );
            if (potion.name == null) potion.name = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));
            Registry.register(BuiltInRegistries.POTION, identifier, new net.minecraft.world.item.alchemy.Potion(
                    new MobEffectInstance(potion.getEffectType(), potion.getEffects().duration * 20, potion.getEffects().amplifier)
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
