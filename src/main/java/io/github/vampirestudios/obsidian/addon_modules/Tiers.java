package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.Tier;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ToolMaterial;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Tiers implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
		Tier tier = BaseGson.GSON.fromJson(new FileReader(file), Tier.class);
		try {
			if (tier == null) return;
			ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(id.modId(), file.getName().replaceAll(".json", ""));

			// Create the ToolMaterial instance
			ToolMaterial toolMaterial = new ToolMaterial(
					TagKey.create(Registries.BLOCK, tier.incorrectBlocksForDrops),
					tier.durability,
					tier.miningSpeed,
					tier.attackDamage,
					tier.enchantability,
					TagKey.create(Registries.ITEM, tier.repairItem.get(0))
			);
			register(ContentRegistries.TOOL_MATERIALS, null, identifier, toolMaterial);

			register(ContentRegistries.TIERS, "tier", identifier, tier);
		} catch (Exception e) {
			failedRegistering("tier", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "item/tier";
	}

}
