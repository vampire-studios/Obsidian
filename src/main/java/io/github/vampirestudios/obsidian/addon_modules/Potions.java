package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.potion.Potion;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Potions implements AddonModule {
	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		Potion potion = AddonFormats.read(addon, file, Potion.class);
		try {
			if (potion == null) return;
			Identifier identifier = Objects.requireNonNullElseGet(
					potion.name,
					() -> Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file))
			);
			if (potion.name == null)
				potion.name = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));

			// Effects resolve against the mob effect registry, so a potion can only be built once the
			// status effects it names exist — Obsidian loads status_effect before item/potion.
			potion.getUnresolvedEffects().forEach(missing ->
					Obsidian.LOGGER.warn("Potion {} names unknown status effect {}; skipping that effect.",
							identifier, missing));

			List<MobEffectInstance> effects = potion.getEffectInstances();
			if (effects.isEmpty()) {
				Obsidian.LOGGER.warn("Potion {} applies no effects; registering it anyway as a plain bottle.",
						identifier);
			}

			Registry.register(BuiltInRegistries.POTION, identifier, new net.minecraft.world.item.alchemy.Potion(
					identifier.getPath(),
					effects.toArray(new MobEffectInstance[0])
			));
			register(ContentRegistries.POTIONS, "potion", identifier, potion);
		} catch (Exception e) {
			failedRegistering("potion", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "item/potion";
	}
}
