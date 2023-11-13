package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.toml.TomlFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.cottonmc.jankson.JanksonFactory;
import io.github.vampirestudios.obsidian.Obsidian;
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
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import org.hjson.JsonValue;
import org.hjson.Stringify;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Ores implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
		io.github.vampirestudios.obsidian.api.obsidian.block.Block block;

		if (addon.getConfigPackInfo() instanceof LegacyObsidianAddonInfo) {
			block = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
		} else {
			ObsidianAddonInfo addonInfo = (ObsidianAddonInfo) addon.getConfigPackInfo();
			if (addonInfo.format == ObsidianAddonInfo.Format.JSON) {
				block = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
			} else if (addonInfo.format == ObsidianAddonInfo.Format.JSON5) {
				JsonObject jsonObject = JanksonFactory.builder().build().load(file);
				block = JanksonFactory.builder().build().fromJson(jsonObject, io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
			} else if (addonInfo.format == ObsidianAddonInfo.Format.YAML) {
				ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
				mapper.findAndRegisterModules();
				block = mapper.readValue(file, io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
			} else if (addonInfo.format == ObsidianAddonInfo.Format.TOML) {
				ObjectMapper mapper = new ObjectMapper(new TomlFactory());
				mapper.findAndRegisterModules();
				block = mapper.readValue(file, io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
			} else if (addonInfo.format == ObsidianAddonInfo.Format.HJSON) {
				block = Obsidian.GSON.fromJson(JsonValue.readHjson(new FileReader(file)).toString(Stringify.FORMATTED), io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
			} else {
				block = null;
			}
		}

		try {
			if (block == null) return;

			ResourceLocation blockId;
			if (block.description != null) {
				blockId = block.description.identifier;
			} else {
				if (block.information.name.id != null) {
					blockId = block.information.name.id;
				} else {
					blockId = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));
					block.information.name.id = new ResourceLocation(id.modId(), file.getName().replaceAll(".json", ""));
				}
			}

			FabricBlockSettings blockSettings;

			if (block.information.parentBlock != null) {
				blockSettings = FabricBlockSettings.copyOf(net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(block.information.parentBlock));
			} else {
				blockSettings = FabricBlockSettings.of();
			}

			if (block.information.getBlockSettings() != null) {
				blockSettings.destroyTime(block.information.getBlockSettings().hardness).explosionResistance(block.information.getBlockSettings().resistance)
						.sound(block.information.getBlockSettings().getBlockSoundGroup())
						.friction(block.information.getBlockSettings().slipperiness)
						.emissiveRendering((state, world, pos) -> block.information.getBlockSettings().is_emissive)
						.lightLevel(blockState -> block.information.getBlockSettings().luminance)
						.speedFactor(block.information.getBlockSettings().velocity_modifier)
						.jumpFactor(block.information.getBlockSettings().jump_velocity_modifier);
				if (block.information.getBlockSettings().randomTicks) blockSettings.randomTicks();
				if (block.information.getBlockSettings().instant_break) blockSettings.instabreak();
				if (!block.information.getBlockSettings().collidable) blockSettings.noCollission();
				if (block.information.getBlockSettings().translucent) blockSettings.noOcclusion();
				if (block.information.getBlockSettings().dynamic_boundaries) blockSettings.dynamicShape();
			}

			FabricItemSettings settings = new FabricItemSettings();
			if (block.information.getItemSettings() != null) {
				settings.stacksTo(block.information.getItemSettings().maxStackSize);
				settings.rarity(Rarity.valueOf(block.information.getItemSettings().rarity.toUpperCase(Locale.ROOT)));
				if (block.information.getItemSettings().durability != 0)
					settings.durability(block.information.getItemSettings().durability);
//				if (!block.information.getItemSettings().wearableSlot.isEmpty() && !block.information.getItemSettings().wearableSlot.isBlank())
//					settings.equipmentSlot(stack -> EquipmentSlot.byName(block.information.getItemSettings().wearableSlot.toLowerCase(Locale.ROOT)));
				if (block.food_information != null)
					settings.food(Registries.FOODS.get(block.food_information.foodComponent));
				if (block.information.getItemSettings().fireproof) settings.fireResistant();
			}

			RegistryHelperBlockExpanded expanded = new RegistryHelperBlockExpanded(REGISTRY_HELPER.modId());

			expanded.registerBlock(new BlockImpl(block, blockSettings), block, blockId.getPath(), settings);

//			if (block.ore_information != null) {
//				ResourceKey<PlacedFeature> key = ResourceKey.create(net.minecraft.core.registries.Registries.PLACED_FEATURE, Utils.appendToPath(blockId, "_ore_feature"));
//				BiomeModifications.addFeature(block.ore_information.biomeSelector(), GenerationStep.Decoration.UNDERGROUND_ORES,
//						key);
//			}

			if (!addon.getConfigPackInfo().hasData) {
				new BlockInitThread(block);
			}

			register(ContentRegistries.ORES, "ore", blockId, block);
		} catch (Exception e) {
			failedRegistering("ore", file.getName(), e);
		}
	}

	@Override
	public String getType() {
		return "block/ores";
	}

}
