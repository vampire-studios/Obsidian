package io.github.vampirestudios.obsidian.addonModules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.potion.Potion;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Potions implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        Potion potion = Obsidian.GSON.fromJson(new FileReader(file), Potion.class);
        try {
            if (potion == null) return;
            Registry.register(Registry.POTION, potion.name,
                    new net.minecraft.potion.Potion(new StatusEffectInstance(potion.getEffectType(), potion.getEffects().duration * 20, potion.getEffects().amplifier)));
            register(POTIONS, "potion", potion.name, potion);
        } catch (Exception e) {
            failedRegistering("potion", potion.name.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "potions";
    }
}
