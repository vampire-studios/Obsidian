package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem;
import io.github.vampirestudios.obsidian.minecraft.obsidian.*;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.CopyPropertiesProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedStateProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Tools implements AddonModule {
	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		ToolItem tool = AddonFormats.read(addon, file, ToolItem.class);
		try {
			if (tool == null) return;

			Identifier identifier = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));
			tool.information.id = identifier;

			if (tool.tool_type == null) throw new IllegalArgumentException("tool_type must be specified");

			Item.Properties settings = new Item.Properties();
			if (!tool.damageable) settings.component(DataComponents.UNBREAKABLE, Unit.INSTANCE);
			if (tool.components != null) ItemModuleHelper.applyAllComponents(settings, tool.components);
			ItemModuleHelper.applyPalette(settings, tool);
			settings.setId(ResourceKey.create(Registries.ITEM, identifier));

			ToolMaterial m = tool.getToolMaterial();
			Item item = switch (tool.tool_type) {
				case PICKAXE ->
						REGISTRY_HELPER.items().registerItem(identifier.getPath(), new ItemImpl(tool, settings.pickaxe(m, 1, 1)));
				case SHOVEL ->
						REGISTRY_HELPER.items().registerItem(identifier.getPath(), new ItemImpl(tool, settings.shovel(m, 1, 1)));
				case HOE ->
						REGISTRY_HELPER.items().registerItem(identifier.getPath(), new ItemImpl(tool, settings.hoe(m, 1, 1)));
				case AXE ->
						REGISTRY_HELPER.items().registerItem(identifier.getPath(), new ItemImpl(tool, settings.axe(m, 1, 1)));
				case BRUSH ->
						REGISTRY_HELPER.items().registerItem(identifier.getPath(), new BrushItemImpl(tool, settings));
				case PAXEL ->
						REGISTRY_HELPER.items().registerItem(identifier.getPath(), new PaxelItemImpl(tool, m, settings));
				case MATTOCK ->
						REGISTRY_HELPER.items().registerItem(identifier.getPath(), new MattockItemImpl(tool, m, settings));
				case HAMMER ->
						REGISTRY_HELPER.items().registerItem(identifier.getPath(), new HammerItemImpl(tool, m, settings));
				case DRILL ->
						REGISTRY_HELPER.items().registerItem(identifier.getPath(), new DrillItemImpl(tool, m, settings));
				case EXCAVATOR ->
						REGISTRY_HELPER.items().registerItem(identifier.getPath(), new ExcavatorItemImpl(tool, m, settings));
				case CHISEL -> registerChisel(tool, m, settings, identifier);
				case FISHING_ROD -> registerFishingRod(tool, settings, identifier);
			};
			CreativeModeTabEvents.modifyOutputEvent(ItemModuleHelper.getCreativeTab(tool)).register(entries -> entries.accept(item));
			register(ContentRegistries.TOOLS, "tool", identifier, tool);
		} catch (Exception e) {
			failedRegistering("tool", file.getName(), e);
		}
	}

	/**
	 * A fishing rod has no tool material and no mining behaviour, so it only needs its durability —
	 * from the item settings if given, otherwise vanilla's.
	 */
	private static Item registerFishingRod(ToolItem tool, Item.Properties settings, Identifier identifier) {
		if (tool.damageable) {
			int durability = tool.information != null && tool.information.getItemSettings() != null
					&& tool.information.getItemSettings().durability != 0
					? tool.information.getItemSettings().durability
					: ToolItem.DEFAULT_ROD_DURABILITY;
			settings.durability(durability);
		}

		return REGISTRY_HELPER.items().registerItem(identifier.getPath(), new FishingRodItemImpl(tool, settings));
	}

	private static Item registerChisel(ToolItem tool, ToolMaterial m, Item.Properties settings,
	                                   Identifier identifier) {
		BlockTransformer transformer = buildChiselTransformer(tool, identifier);
		if (transformer != null) settings.component(DataComponents.BLOCK_TRANSFORMER, Holder.direct(transformer));

		return REGISTRY_HELPER.items().registerItem(identifier.getPath(),
				new ItemImpl(tool, settings.pickaxe(m, -2f, 2.0f)));
	}

	private static BlockTransformer buildChiselTransformer(ToolItem tool, Identifier identifier) {
		if (tool.chisel_mappings == null) return null;

		List<BlockTransformer.BlockTransformData> transforms = new ArrayList<>();
		for (ToolItem.ChiselMapping mapping : tool.chisel_mappings) {
			try {
				BlockPredicate source = resolveSource(mapping.from, identifier);
				Holder<BlockStateProvider> target = resolveTarget(mapping.to, identifier);
				if (source == null || target == null) continue;

				BlockTransformer.BlockTransformData.Builder builder =
						BlockTransformer.BlockTransformData.builder(
								RuleBasedStateProvider.ifTrueThenProvide(source, target.value()));

				if (mapping.sound != null) {
					BuiltInRegistries.SOUND_EVENT.get(Identifier.tryParse(mapping.sound)).ifPresent(builder::sound);
				}
				mapping.applyTo(builder, identifier);
				if (mapping.reversible) {
					Obsidian.LOGGER.warn("[Obsidian] Chisel mapping {} -> {} in {} is marked reversible, but "
									+ "BLOCK_TRANSFORMER has no inverse. Declare the reverse mapping on the reversal item.",
							mapping.from, mapping.to, identifier);
				}
				if (mapping.dropped_item != null) {
					Obsidian.LOGGER.warn("[Obsidian] Chisel mapping {} -> {} in {} sets dropped_item, which "
									+ "BLOCK_TRANSFORMER does not support; use \"loot\" with a loot table instead.",
							mapping.from, mapping.to, identifier);
				}

				transforms.add(builder.build());
			} catch (Exception ex) {
				Obsidian.LOGGER.warn("[Obsidian] Failed to build chisel mapping {} -> {} for {}: {}",
						mapping.from, mapping.to, identifier, ex.getMessage());
			}
		}

		return transforms.isEmpty() ? null : new BlockTransformer(transforms);
	}

	/** Ops backed by the built-in registries, so holder- and tag-based codecs resolve at registration time. */
	private static final RegistryOps<JsonElement> CODEC_OPS =
			RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY));

	/**
	 * A string is treated as a block id; anything else is decoded with vanilla's {@code BlockPredicate}
	 * codec, which gives packs tag matching, {@code any_of}, {@code all_of} and the rest for free.
	 */
	private static BlockPredicate resolveSource(JsonElement from, Identifier identifier) {
		if (from == null) {
			Obsidian.LOGGER.warn("[Obsidian] Chisel mapping in {} is missing \"from\".", identifier);
			return null;
		}
		if (from.isJsonPrimitive()) {
			Identifier fromId = Identifier.tryParse(from.getAsString());
			Block fromBlock = fromId == null ? null : BuiltInRegistries.BLOCK.get(fromId).map(Holder::value).orElse(null);
			if (fromBlock == null) {
				Obsidian.LOGGER.warn("[Obsidian] Chisel mapping in {} has unknown \"from\" block {}.", identifier, from);
				return null;
			}
			return BlockPredicate.matchesBlocks(fromBlock);
		}
		return BlockPredicate.CODEC.parse(CODEC_OPS, from)
				.resultOrPartial(error -> Obsidian.LOGGER.warn(
						"[Obsidian] Chisel mapping in {} has an invalid \"from\" predicate: {}", identifier, error))
				.orElse(null);
	}

	/**
	 * A string is treated as a block id whose properties are copied from the block being replaced; anything
	 * else is decoded with vanilla's {@code BlockStateProvider} codec (weighted, random, rotated, noise...).
	 */
	private static Holder<BlockStateProvider> resolveTarget(JsonElement to, Identifier identifier) {
		if (to == null) {
			Obsidian.LOGGER.warn("[Obsidian] Chisel mapping in {} is missing \"to\".", identifier);
			return null;
		}
		if (to.isJsonPrimitive()) {
			Identifier toId = Identifier.tryParse(to.getAsString());
			Block toBlock = toId == null ? null : BuiltInRegistries.BLOCK.get(toId).map(Holder::value).orElse(null);
			if (toBlock == null) {
				Obsidian.LOGGER.warn("[Obsidian] Chisel mapping in {} has unknown \"to\" block {}.", identifier, to);
				return null;
			}
			// Carries the existing state's properties over, matching what the old withPropertiesOf()
			// conversion did — without it a chiselled stair would lose its facing.
			return Holder.direct(new CopyPropertiesProvider(toBlock));
		}
		return BlockStateProvider.CODEC.parse(CODEC_OPS, to)
				.resultOrPartial(error -> Obsidian.LOGGER.warn(
						"[Obsidian] Chisel mapping in {} has an invalid \"to\" provider: {}", identifier, error))
				.orElse(null);
	}

	@Override
	public String getType() {
		return "item/tool";
	}
}
