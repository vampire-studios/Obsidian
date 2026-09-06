package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.statusEffects.StatusEffect;
import io.github.vampirestudios.obsidian.minecraft.obsidian.StatusEffectImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import java.io.File;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class StatusEffects implements AddonModule {
	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		StatusEffect statusEffect = AddonFormats.read(addon, file, StatusEffect.class);
		try {
			if (statusEffect == null) return;
			Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));
			Registry.register(BuiltInRegistries.MOB_EFFECT, identifier, new StatusEffectImpl(statusEffect));
			register(ContentRegistries.STATUS_EFFECTS, "status_effect", identifier, statusEffect);
		} catch (Exception e) {
			failedRegistering("status_effect", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "status_effect";
	}
}
