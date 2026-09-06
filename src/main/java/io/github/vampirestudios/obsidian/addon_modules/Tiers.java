package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.Tier;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ToolMaterial;

import java.io.File;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Tiers implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		Tier tier = AddonFormats.read(addon, file, Tier.class);
		try {
			if (tier == null) return;
			Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));

			ToolMaterial toolMaterial = new ToolMaterial(
					TagKey.create(Registries.BLOCK, tier.incorrectBlocksForDrops),
					tier.durability,
					tier.miningSpeed,
					tier.attackDamage,
					tier.enchantability,
					repairTag(tier, identifier)
			);
			register(ContentRegistries.TOOL_MATERIALS, null, identifier, toolMaterial);

			register(ContentRegistries.TIERS, "tier", identifier, tier);
		} catch (Exception e) {
			failedRegistering("tier", file.getName(), e);
		}
	}

	private static TagKey<net.minecraft.world.item.Item> repairTag(Tier tier, Identifier tierId) {
		Identifier tag = tier.repairTag == null
				? Identifier.fromNamespaceAndPath(tierId.getNamespace(), tierId.getPath() + "_repair_items")
				: tier.repairTag;

		return TagKey.create(Registries.ITEM, tag);
	}

	@Override
	public String getType() {
		return "item/tier";
	}

}
