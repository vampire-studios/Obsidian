package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.ItemGroup;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class LegacyItemGroups implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		ItemGroup itemGroup = AddonFormats.read(addon, file, ItemGroup.class);
		try {
			if (itemGroup == null) return;

			Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));
			itemGroup.id = identifier;

			CreativeModeTab itemGroup1 = FabricCreativeModeTab.builder()
					.icon(() -> new ItemStack(BuiltInRegistries.ITEM.getValue(itemGroup.icon)))
					.title(Component.translatable("itemGroup." + identifier.toLanguageKey()))
					.displayItems((displayContext, entries) -> {
						if (itemGroup.tags != null) {
							for (Map.Entry<String, Identifier> tag : itemGroup.tags.entrySet()) {
								if (tag.getKey().equals("block")) {
									TagKey<Block> blockTagKey = TagKey.create(net.minecraft.core.registries.Registries.BLOCK, tag.getValue());
									entries.accept(BuiltInRegistries.BLOCK.getValue(blockTagKey.location()));
								}
								if (tag.getKey().equals("item")) {
									TagKey<Item> blockTagKey = TagKey.create(net.minecraft.core.registries.Registries.ITEM, tag.getValue());
									entries.accept(BuiltInRegistries.ITEM.getValue(blockTagKey.location()));
								}
							}
						}
						if (itemGroup.items != null) {
							for (Identifier item : itemGroup.items) {
								entries.accept(BuiltInRegistries.ITEM.getValue(item));
							}
						}
						if (itemGroup.blocks != null) {
							for (Identifier block : itemGroup.blocks) {
								entries.accept(BuiltInRegistries.BLOCK.getValue(block));
							}
						}
						if (itemGroup.opItems != null && displayContext.hasPermissions()) {
							for (Identifier item : itemGroup.opItems) {
								entries.accept(BuiltInRegistries.ITEM.getValue(item));
							}
						}
						if (itemGroup.opBlocks != null && displayContext.hasPermissions()) {
							for (Identifier block : itemGroup.opBlocks) {
								entries.accept(BuiltInRegistries.BLOCK.getValue(block));
							}
						}
						if (itemGroup.featureSetItems != null) {
							for (Map.Entry<String, Identifier> entry : itemGroup.featureSetItems.entrySet()) {
								if (entry.getKey().equals("vanilla") && displayContext.enabledFeatures().contains(FeatureFlags.VANILLA)) {
									entries.accept(BuiltInRegistries.ITEM.getValue(entry.getValue()));
								}
							}
						}
						if (itemGroup.featureSetBlocks != null) {
							for (Map.Entry<String, Identifier> entry : itemGroup.featureSetBlocks.entrySet()) {
								if (entry.getKey().equals("vanilla") && displayContext.enabledFeatures().contains(FeatureFlags.VANILLA)) {
									entries.accept(BuiltInRegistries.BLOCK.getValue(entry.getValue()));
								}
							}
						}
					})
					.build();
			Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, identifier, itemGroup1);
			register(ContentRegistries.ITEM_GROUPS, "item_group", identifier, itemGroup);
		} catch (Exception e) {
			failedRegistering("item_group", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "item_group";
	}

}