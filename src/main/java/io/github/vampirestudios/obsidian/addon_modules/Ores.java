package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.toml.TomlFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.RegistryHelperBlockExpanded;
import io.github.vampirestudios.obsidian.configPack.LegacyObsidianAddonInfo;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddonInfo;
import io.github.vampirestudios.obsidian.minecraft.obsidian.BlockImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.threadhandlers.data.BlockInitThread;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.hjson.JsonValue;
import org.hjson.Stringify;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Ores implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
		io.github.vampirestudios.obsidian.api.obsidian.block.Block block;

		if (addon.getConfigPackInfo() instanceof LegacyObsidianAddonInfo) {
			block = BaseGson.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
		} else {
			ObsidianAddonInfo addonInfo = (ObsidianAddonInfo) addon.getConfigPackInfo();
			if (addonInfo.format == ObsidianAddonInfo.Format.JSON) {
				block = BaseGson.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
			} else if (addonInfo.format == ObsidianAddonInfo.Format.JSON5) {
				JsonObject jsonObject = Jankson.builder().build().load(file);
				block = Jankson.builder().build().fromJson(jsonObject, io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
			} else if (addonInfo.format == ObsidianAddonInfo.Format.YAML) {
				ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
				mapper.findAndRegisterModules();
				block = mapper.readValue(file, io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
			} else if (addonInfo.format == ObsidianAddonInfo.Format.TOML) {
				ObjectMapper mapper = new ObjectMapper(new TomlFactory());
				mapper.findAndRegisterModules();
				block = mapper.readValue(file, io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
			} else if (addonInfo.format == ObsidianAddonInfo.Format.HJSON) {
				block = BaseGson.GSON.fromJson(JsonValue.readHjson(new FileReader(file)).toString(Stringify.FORMATTED), io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
			} else {
				block = null;
			}
		}

		try {
			if (block == null) return;

			Identifier blockId = Identifier.fromNamespaceAndPath(id.modId(), file.getName().replace(".json", ""));
			block.information.id = blockId;

			BlockBehaviour.Properties blockProps = createBlockProperties(block);
			Item.Properties itemProps = createItemProperties(block).setId(ResourceKey.create(net.minecraft.core.registries.Registries.ITEM, blockId));
			ResourceKey<CreativeModeTab> tab = getCreativeTab(block);
			RegistryHelperBlockExpanded registry = new RegistryHelperBlockExpanded(id.modId());

			registry.registerBlock(new BlockImpl(block, blockProps), block, blockId.getPath(), itemProps, tab);

			if (!addon.getConfigPackInfo().hasData) {
				new BlockInitThread(block);
			}

			register(ContentRegistries.ORES, "ore", blockId, block);
		} catch (Exception e) {
			failedRegistering("ore", file.getName(), e);
		}
	}

	private BlockBehaviour.Properties createBlockProperties(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
		BlockBehaviour.Properties props = block.information.parentBlock != null
				? BlockBehaviour.Properties.ofLegacyCopy(BuiltInRegistries.BLOCK.getValue(block.information.parentBlock))
				: BlockBehaviour.Properties.of();

		props.setId(ResourceKey.create(net.minecraft.core.registries.Registries.BLOCK, block.information.id));

		if (block.information.getBlockSettings() != null) {
			var settings = block.information.getBlockSettings();
			props.destroyTime(settings.hardness)
					.explosionResistance(settings.resistance)
					.mapColor(settings.getMapColor())
					.pushReaction(settings.getPushReaction())
					.sound(settings.getBlockSoundGroup())
					.friction(settings.slipperiness)
					.emissiveRendering((state, level, pos) -> settings.is_emissive)
					.lightLevel(state -> settings.luminance)
					.speedFactor(settings.velocity_modifier)
					.jumpFactor(settings.jump_velocity_modifier)
					.noOcclusion();

			if (settings.randomTicks) props.randomTicks();
			if (settings.instant_break) props.instabreak();
			if (!settings.collidable) props.noCollision();
//            if (settings.translucent) props.noOcclusion();
			if (settings.dynamic_boundaries) props.dynamicShape();
		}

		return props;
	}

	private Item.Properties createItemProperties(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
		Item.Properties props = new Item.Properties()
				.useBlockDescriptionPrefix();

		// --------------------------------------------------
		// 1. Legacy / JSON item settings (base defaults)
		// --------------------------------------------------
		if (block.information.getItemSettings() != null) {
			var settings = block.information.getItemSettings();

			props.stacksTo(settings.maxStackSize)
					.rarity(Rarity.valueOf(settings.rarity.toUpperCase(Locale.ROOT)));

			if (settings.durability != 0) {
				props.durability(settings.durability);
			}

			if (settings.fireproof) {
				props.fireResistant();
			}
		}

		// --------------------------------------------------
		// 2. Food definition
		// --------------------------------------------------
		if (block.food_information != null) {
			props.food(Registries.FOODS.getValue(block.food_information.foodComponent));
		}

		// --------------------------------------------------
		// 3. Apply DataComponents LAST (override layer)
		// --------------------------------------------------
		if (block.components != null) {
			applyAllComponents(props, block.components);
		}

		return props;
	}

	@SuppressWarnings("unchecked")
	static <T> void applyAllComponents(Item.Properties props, DataComponentPatch map) {
		for (var e : map.entrySet()) {
			var type = (DataComponentType<T>) e.getKey();
			var opt  = (Optional<T>) e.getValue();
			opt.ifPresent(v -> props.component(type, v));
		}
	}

	private ResourceKey<CreativeModeTab> getCreativeTab(io.github.vampirestudios.obsidian.api.obsidian.block.Block block) {
		// 1) Components override everything
		var comps = block.components;
//		if (comps != null) {
//			var opt = comps.get(OItemComponents.CREATIVE_TAB); // Optional<Identifier> (based on your usage)
//			if (opt != null && opt.isPresent()) {
//				Identifier id = opt.get();
//				return ResourceKey.create(net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB, id);
//			}
//		}

		// 2) Then item settings
		var settings = block.information.getItemSettings();
		if (settings != null) {
			var group = settings.getItemGroup();
			if (group != null) return group;

			var parent = settings.getParentSettings();
			if (parent != null && parent.getItemGroup() != null) return parent.getItemGroup();
		}

		// 3) Fallback
		return CreativeModeTabs.BUILDING_BLOCKS;
	}

	@Override
	public String getType() {
		return "block/ore";
	}

}
