package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.minecraft.obsidian.BlockImpl;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.Registries;
import io.github.vampirestudios.obsidian.threadhandlers.data.BlockInitThread;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.minecraft.item.Item;
import net.minecraft.util.Holder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.BiomePlacementModifier;
import net.minecraft.world.gen.decorator.HeightRangePlacementModifier;
import net.minecraft.world.gen.decorator.InSquarePlacementModifier;
import net.minecraft.world.gen.decorator.RarityFilterPlacementModifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.util.ConfiguredFeatureUtil;
import net.minecraft.world.gen.feature.util.PlacedFeatureUtil;
import org.quiltmc.qsl.block.extensions.api.QuiltBlockSettings;
import org.quiltmc.qsl.worldgen.biome.api.BiomeModifications;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Ores implements AddonModule {

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
		io.github.vampirestudios.obsidian.api.obsidian.block.Block block = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
		try {
			if (block == null) return;

			Identifier blockId;
			if (block.description != null) {
				blockId = block.description.identifier;
			} else {
				if (block.information.name.id != null) {
					blockId = block.information.name.id;
				} else {
					blockId = new Identifier(id.modId(), file.getName().replaceAll(".json", ""));
					block.information.name.id = new Identifier(id.modId(), file.getName().replaceAll(".json", ""));
				}
			}

			QuiltBlockSettings blockSettings;

			if (block.information.parentBlock != null) {
				blockSettings = QuiltBlockSettings.copyOf(Registry.BLOCK.get(block.information.parentBlock));
			} else {
				blockSettings = QuiltBlockSettings.of(block.information.blockProperties.getMaterial());
			}

			blockSettings.hardness(block.information.blockProperties.hardness).resistance(block.information.blockProperties.resistance)
					.sounds(block.information.blockProperties.getBlockSoundGroup())
					.slipperiness(block.information.blockProperties.slipperiness)
					.emissiveLighting((state, world, pos) -> block.information.blockProperties.is_emissive)
					.luminance(block.information.blockProperties.luminance)
					.velocityMultiplier(block.information.blockProperties.velocity_modifier)
					.jumpVelocityMultiplier(block.information.blockProperties.jump_velocity_modifier);
			if (block.information.blockProperties.randomTicks) blockSettings.ticksRandomly();
			if (block.information.blockProperties.instant_break) blockSettings.breakInstantly();
			if (!block.information.blockProperties.collidable) blockSettings.noCollision();
			if (block.information.blockProperties.translucent) blockSettings.nonOpaque();
			if (block.information.blockProperties.dynamic_boundaries) blockSettings.dynamicBounds();

			Item.Settings settings = new Item.Settings().group(block.information.itemProperties.getItemGroup());
			if (block.food_information != null) settings.food(Registries.FOOD_COMPONENTS.get(block.food_information.foodComponent));
			if (block.information.itemProperties.fireproof) settings.fireproof();

			net.minecraft.block.Block blockImpl = REGISTRY_HELPER.registerBlock(new BlockImpl(block, blockSettings), block, blockId.getPath(), settings);

			if (block.ore_information != null) {
				RegistryKey<PlacedFeature> key = RegistryKey.of(Registry.PLACED_FEATURE_KEY, Utils.appendToPath(blockId, "_ore_feature"));
				Holder<ConfiguredFeature<OreFeatureConfig, ?>> feature = ConfiguredFeatureUtil.register(key.getValue().toString(),
						Feature.ORE,
						new OreFeatureConfig(
								block.ore_information.ruleTest(),
								blockImpl.getDefaultState(),
								block.ore_information.size
						)
				);
				PlacedFeatureUtil.register(key.getValue().toString(),
						feature,
						InSquarePlacementModifier.getInstance(),
						HeightRangePlacementModifier.create(block.ore_information.heightRange()),
						RarityFilterPlacementModifier.create(block.ore_information.chance),
						BiomePlacementModifier.getInstance()
				);

				BiomeModifications.addFeature(block.ore_information.biomeSelector(), GenerationStep.Feature.UNDERGROUND_ORES,
						key);
			}

			/*Obsidian.registerDataPack(Utils.appendToPath(blockId, "_data"), serverResourcePackBuilder -> {
				serverResourcePackBuilder.addLootTable(blockId, lootTableBuilder -> {
					lootTableBuilder.type(new Identifier("block"));
					lootTableBuilder.pool(pool -> {
						pool.rolls(1);
						pool.bonusRolls(0);
						pool.entry(entry -> {
							entry.type(new Identifier("alternatives"));
							if (block.drop_information != null && block.drop_information.drops != null) {
								for (DropInformation.Drop drop : block.drop_information.drops) {
									if (drop.dropsIfSilkTouch) {
										entry.child(entry1 -> {
											entry1.type(new Identifier("item"));
											entry1.name(drop.name);
											entry1.condition(new Identifier("minecraft:match_tool"), jsonObjectBuilder -> {
												JsonObject predicate = new JsonObject();

												JsonArray enchantments = new JsonArray();

												JsonObject predicate1 = new JsonObject();
												predicate1.addProperty("enchantment", "minecraft:silk_touch");

												JsonObject levels = new JsonObject();
												levels.addProperty("min", 1);
												predicate1.add("levels", levels);
												enchantments.add(predicate1);

												predicate.add("enchantments", enchantments);

												jsonObjectBuilder.add("predicate", predicate);
											});
										});
									} else {
										entry.child(entry1 -> {
											entry1.type(new Identifier("item"));
											entry1.name(drop.name);
											entry1.function(new Identifier("minecraft:apply_bonus"), function -> {
												function.add("enchantment", "minecraft:fortune");
												function.add("formula", "minecraft:ore_drops");
											});
											entry1.function(new Identifier("minecraft:explosion_decay"), function -> {});
										});
									}
								}
							}
						});
						if (block.drop_information != null && block.drop_information.survivesExplosion)
							pool.condition(new Identifier("survives_explosion"), json -> {});
					});
				});
				try {
					serverResourcePackBuilder.dumpResources("testing", "data");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});*/

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
		return "blocks/ores";
	}

}
