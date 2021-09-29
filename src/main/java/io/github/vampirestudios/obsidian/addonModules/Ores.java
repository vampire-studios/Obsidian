package io.github.vampirestudios.obsidian.addonModules;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.minecraft.obsidian.BlockImpl;
import io.github.vampirestudios.obsidian.minecraft.obsidian.CustomBlockItem;
import io.github.vampirestudios.obsidian.threadhandlers.data.BlockInitThread;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.item.Item;
import net.minecraft.structure.rule.TagMatchRuleTest;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Optional;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.ORES;

public class Ores implements AddonModule {

	@Override
	public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
		io.github.vampirestudios.obsidian.api.obsidian.block.Block block = Obsidian.GSON.fromJson(new FileReader(file), io.github.vampirestudios.obsidian.api.obsidian.block.Block.class);
		try {
			if (block == null) return;
			FabricBlockSettings blockSettings = FabricBlockSettings.of(block.information.getMaterial()).sounds(block.information.getBlockSoundGroup())
					.strength(block.information.hardness, block.information.resistance).drops(block.information.drop)
					.slipperiness(block.information.slipperiness).emissiveLighting((state, world, pos) -> block.information.is_emissive)
					.nonOpaque();
			net.minecraft.block.Block blockImpl = REGISTRY_HELPER.registerBlockWithoutItem(new BlockImpl(block, blockSettings), block.information.name.id.getPath());
			REGISTRY_HELPER.registerItem(new CustomBlockItem(block, blockImpl, new Item.Settings().group(block.information.getItemGroup())), block.information.name.id.getPath());

			ConfiguredFeature<?, ?> feature = Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, Utils.appendToPath(block.information.name.id, "_ore_feature"),
					Feature.ORE.configure(
							new OreFeatureConfig(
									new TagMatchRuleTest(/*BlockTags.getTagGroup().getTagOrEmpty(block.ore_information.target_state.tag)*/BlockTags.BASE_STONE_OVERWORLD),
									blockImpl.getDefaultState(),
									8
							)
					).uniformRange(
						/*YOffset.aboveBottom(block.ore_information.config.above_bottom_offset),
						YOffset.belowTop(block.ore_information.config.below_top_offset)*/
						YOffset.fixed(10),
						YOffset.fixed(50)
					).spreadHorizontally()/*.applyChance(12)*/);

			Optional<RegistryKey<ConfiguredFeature<?, ?>>> optional = BuiltinRegistries.CONFIGURED_FEATURE.getKey(feature);
			BiomeModifications.create(Utils.appendToPath(block.information.name.id, "_ore_feature"))
					.add(ModificationPhase.ADDITIONS, BiomeSelectors.builtIn(),
							biomeModificationContext -> biomeModificationContext.getGenerationSettings().addFeature(
									GenerationStep.Feature.UNDERGROUND_ORES,
									optional.get()
							));

			Obsidian.registerDataPack(Utils.appendToPath(block.information.name.id, "_data"), serverResourcePackBuilder ->
					serverResourcePackBuilder.addLootTable(block.information.name.id, lootTableBuilder -> {
						lootTableBuilder.type(new Identifier("block"));
						lootTableBuilder.pool(pool -> {
							pool.rolls(1);
							pool.bonusRolls(0);
							pool.entry(entry -> {
								for (Identifier identifier : block.dropInformation.drops) {
									entry.type(new Identifier("alternatives"));
									if (identifier.equals(block.information.name.id)) {
										entry.child(entry1 -> {
											entry.name(identifier);
											if (block.dropInformation.requiresSilkTouch) {
												entry.condition(new Identifier("match_tool"), jsonObjectBuilder -> {
													JsonObject predicate = new JsonObject();

													JsonArray enchantments = new JsonArray();

													JsonObject enchantmentEntry = new JsonObject();
													enchantmentEntry.addProperty("enchantment", "minecraft:silk_touch");

													JsonObject level = new JsonObject();
													level.addProperty("min", 1);
													enchantmentEntry.add("level", level);

													enchantments.add(enchantmentEntry);

													predicate.add("enchantments", enchantments);
													jsonObjectBuilder.add("predicate", predicate);
												});
											} else {
												entry.function(new Identifier("apply_bonus"), function -> {
													function.add("enchantment", "minecraft:fortune");
													function.add("formula", "minecraft:ore_drops");
												});
											}
										});
									} else {
										entry.child(entry1 -> {
											entry.type(new Identifier("item"));
											entry.name(identifier);
											entry.function(new Identifier("apply_bonus"), function -> {
												function.add("enchantment", "minecraft:fortune");
												function.add("formula", "minecraft:ore_drops");
											});
										});
									}
								}
							});
							if (block.ore_information.survivesExplosion)
								pool.condition(
								        new Identifier("survives_explosion"),
										jsonObjectBuilder -> {}
                                );
						});
					})
			);

			if (!addon.getConfigPackInfo().hasData) {
				new BlockInitThread(block);
			}

			register(ORES, "ore", block.information.name.id, block);
		} catch (Exception e) {
			failedRegistering("ore", block.information.name.id.toString(), e);
		}
	}

	@Override
	public String getType() {
		return "blocks/ores";
	}

}
