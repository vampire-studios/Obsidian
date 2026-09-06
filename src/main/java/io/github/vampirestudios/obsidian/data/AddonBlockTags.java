package io.github.vampirestudios.obsidian.data;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.BlockSettings;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.block.CompanionBlocks;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.vampirestudios.packwright.api.RuntimeResourcePack;
import net.vampirestudios.packwright.api.SidedPackwrightCallback;
import net.vampirestudios.packwright.data.tags.Tag;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * The mining tags that decide which tool breaks an addon's blocks, and which tier of it is good enough.
 *
 * <p>The game does not read a tool off the block. It asks the block's tags: {@code minecraft:mineable/pickaxe}
 * and friends say which tool mines it quickly, and {@code minecraft:needs_iron_tool} and friends say how
 * good that tool has to be. Nothing wrote either for addon blocks, so {@code "requires_tool": true} used
 * to be a trap — no tool was ever the correct one, and the block dropped nothing with any of them, at
 * bare-hand speed. Declaring {@code "mineable": "pickaxe"} is what closes that gap.
 *
 * <p>The tags are appended rather than replaced, so vanilla's own contents survive.
 */
public final class AddonBlockTags {

	private AddonBlockTags() {
	}

	/** The tools a block can be declared mineable with, and the tag each one reads. */
	private static final Set<String> TOOLS = Set.of("pickaxe", "axe", "shovel", "hoe");

	/** The tiers that have a {@code needs_*_tool} tag. Wood and gold are the absence of one. */
	private static final Set<String> TIERS = Set.of("stone", "iron", "diamond");

	public static void register() {
		SidedPackwrightCallback.BETWEEN_MODS_AND_USER.register((type, resources) -> {
			if (type != PackType.SERVER_DATA) return;

			// Kept in declaration order so the written tags are stable between runs.
			Map<Identifier, Set<Identifier>> tags = new LinkedHashMap<>();
			for (Block block : ContentRegistries.BLOCKS) collect(block, tags);
			if (tags.isEmpty()) return;

			RuntimeResourcePack pack = RuntimeResourcePack.create(Obsidian.id("block_mining_tags"));
			tags.forEach((tagId, blocks) -> {
				// not replacing: the pack sits after vanilla and mods, so these are appended to the tag
				Tag tag = Tag.tag();
				blocks.forEach(tag::add);
				pack.addTag(tagId, tag);
			});
			resources.add(pack);

			Obsidian.LOGGER.debug("[Obsidian] Wrote {} block mining tag(s)", tags.size());
		});
	}

	/** Adds one declaration's block, and each of its companions, to the tags their settings ask for. */
	private static void collect(Block block, Map<Identifier, Set<Identifier>> tags) {
		if (block.information == null || block.information.id == null) return;

		add(block.information.id, block.information.getBlockSettings(), tags);

		for (CompanionBlocks.Declared variant : CompanionBlocks.declared(block)) {
			// A companion that declared settings of its own is mined the way those say; the rest follow
			// the block they came from, which is how they were registered in the first place.
			BlockSettings settings = variant.options().getBlockSettings();
			add(variant.id(), settings != null ? settings : block.information.getBlockSettings(), tags);
		}
	}

	private static void add(Identifier blockId, BlockSettings settings, Map<Identifier, Set<Identifier>> tags) {
		if (settings == null) return;
		// BLOCK is a defaulted registry, so an unknown id comes back as air rather than null. A block that
		// failed to register has no tag to be in.
		if (!BuiltInRegistries.BLOCK.containsKey(blockId)) return;

		String tool = normalise(settings.mineable);
		if (tool == null) {
			if (settings.requiresTool) {
				Obsidian.LOGGER.warn("[Obsidian] {} sets \"requires_tool\" without a \"mineable\" tool, so no tool "
						+ "can be the correct one and it would drop nothing. Add \"mineable\": \"pickaxe\" (or axe, "
						+ "shovel, hoe) to it.", blockId);
			}
			return;
		}
		if (!TOOLS.contains(tool)) {
			Obsidian.LOGGER.warn("[Obsidian] Unknown \"mineable\" tool \"{}\" on {}; expected one of {}.",
					settings.mineable, blockId, TOOLS);
			return;
		}

		tags.computeIfAbsent(mineable(tool), id -> new LinkedHashSet<>()).add(blockId);

		String tier = normalise(settings.toolTier);
		if (tier == null) return;
		if (!TIERS.contains(tier)) {
			// Wood and gold are the bottom of the ladder, which every tool already clears, so they have no
			// tag of their own — naming one is a mistake worth saying out loud rather than silently dropping.
			Obsidian.LOGGER.warn("[Obsidian] Unknown \"tool_tier\" \"{}\" on {}; expected one of {}.",
					settings.toolTier, blockId, TIERS);
			return;
		}
		if (!settings.requiresTool) {
			Obsidian.LOGGER.warn("[Obsidian] {} sets \"tool_tier\" without \"requires_tool\", so every tier already "
					+ "drops it and the tier does nothing.", blockId);
		}
		tags.computeIfAbsent(needsTool(tier), id -> new LinkedHashSet<>()).add(blockId);
	}

	private static String normalise(String value) {
		if (value == null || value.isBlank()) return null;
		return value.trim().toLowerCase(Locale.ROOT);
	}

	/** {@code data/minecraft/tags/block/mineable/<tool>.json} */
	private static Identifier mineable(String tool) {
		return Identifier.withDefaultNamespace("block/mineable/" + tool);
	}

	/** {@code data/minecraft/tags/block/needs_<tier>_tool.json} */
	private static Identifier needsTool(String tier) {
		return Identifier.withDefaultNamespace("block/needs_" + tier + "_tool");
	}
}
