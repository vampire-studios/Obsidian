package io.github.vampirestudios.obsidian.addonModules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.statusEffects.StatusEffect;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.minecraft.obsidian.StatusEffectImpl;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ConfigHelper.*;

public class StatusEffects implements AddonModule {
	@Override
	public void init(ObsidianAddon addon, ModIdAndAddonPath id) throws FileNotFoundException {
		File file = addon.getFile();
		StatusEffect statusEffect = Obsidian.GSON.fromJson(new FileReader(file), StatusEffect.class);
		try {
			if (statusEffect == null) return;
			String color1 = statusEffect.color.replace("#", "").replace("0x", "");
			Registry.register(Registry.STATUS_EFFECT, statusEffect.name.id, new StatusEffectImpl(statusEffect.getStatusEffectType(), Integer.parseInt(color1, 16)));
			register(STATUS_EFFECTS, "status_effect", statusEffect.name.translated.get("en_us"), statusEffect);
		} catch (Exception e) {
			failedRegistering("status_effect", statusEffect.name.translated.get("en_us"), e);
		}
	}

	@Override
	public String getType() {
		return "status_effects";
	}
}
