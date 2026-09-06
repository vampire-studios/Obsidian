package io.github.vampirestudios.obsidian.data;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.block.CompanionBlocks;
import io.github.vampirestudios.obsidian.api.obsidian.block.DropInformation;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.vampirestudios.packwright.api.RuntimeResourcePack;
import net.vampirestudios.packwright.api.SidedPackwrightCallback;
import net.vampirestudios.packwright.data.loot.Condition;
import net.vampirestudios.packwright.data.loot.Entry;
import net.vampirestudios.packwright.data.loot.LootFunction;
import net.vampirestudios.packwright.data.loot.LootTable;
import net.vampirestudios.packwright.data.loot.Pool;
import net.vampirestudios.packwright.data.loot.providers.number.NumberProvider;
import net.vampirestudios.packwright.data.predicate.BlockPredicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The loot tables that make an addon's blocks drop something when they are broken.
 *
 * <p>A registered block with no loot table drops nothing at all, so without this every block an addon
 * adds was creative-only in practice. Each one gets the table vanilla would write for it: itself, once,
 * destroyed by explosions — or, for a slab, two of itself when the broken block was a double slab.
 *
 * <p>A pack that ships its own loot tables in a data pack, or wants a block to genuinely drop nothing,
 * turns this off per block with {@code "drop_information": { "generate_loot_table": false }}.
 */
public final class AddonBlockLoot {

	private AddonBlockLoot() {
	}

	/** Vanilla's own "the tool can silk touch" predicate, which its ore tables reference by id. */
	private static final Identifier CAN_SILK_TOUCH =
			Identifier.withDefaultNamespace("tool/can_silk_touch");

	public static void register() {
		SidedPackwrightCallback.BETWEEN_MODS_AND_USER.register((type, resources) -> {
			if (type != PackType.SERVER_DATA) return;

			RuntimeResourcePack pack = RuntimeResourcePack.create(Obsidian.id("block_loot"));
			int written = 0;
			for (Block block : ContentRegistries.BLOCKS) written += generate(pack, block);

			if (written == 0) return;
			resources.add(pack);
			Obsidian.LOGGER.debug("[Obsidian] Wrote {} block loot table(s)", written);
		});
	}

	/** @return how many tables were written for this declaration */
	private static int generate(RuntimeResourcePack pack, Block block) {
		DropInformation drops = block.dropInformation;
		if (drops != null && !drops.generateLootTable) return 0;
		if (block.information == null || block.information.id == null) return 0;

		Identifier blockId = block.information.id;
		// A block pointed at someone else's loot table has nothing of its own to write — and writing at
		// that key would replace the very table it was pointed at.
		int written = drops != null && drops.lootTable != null ? 0 : write(pack, blockId, drops, false);

		for (CompanionBlocks.Declared variant : CompanionBlocks.declared(block)) {
			// A variant pointed at a table of its own is where the game will look instead, and writing
			// here would replace it; one that asked for no table at all wants to drop nothing.
			if (!variant.generatesLootTable()) continue;
			if (drops != null && drops.lootTableFor(variant.type().key) != null) continue;

			written += write(pack, variant.id(), drops, variant.type() == CompanionBlocks.Type.SLAB);
		}
		return written;
	}

	/**
	 * Writes one block's table, at whatever loot table the registered block actually asks for — which is
	 * where the game will look, rather than a path guessed from the block id.
	 *
	 * @param slab whether a double slab should drop two, the one place vanilla's block table is not a
	 *             plain "drops itself"
	 */
	private static int write(RuntimeResourcePack pack, Identifier blockId, DropInformation drops, boolean slab) {
		// BLOCK is a defaulted registry, so an unknown id comes back as air rather than null.
		if (!BuiltInRegistries.BLOCK.containsKey(blockId)) return 0;

		ResourceKey<net.minecraft.world.level.storage.loot.LootTable> key =
				BuiltInRegistries.BLOCK.getValue(blockId).getLootTable().orElse(null);
		// A block registered with no loot table is meant to drop nothing; writing a table for it would
		// not be read anyway, since nothing points at one.
		if (key == null) return 0;

		LootTable table;
		try {
			table = table(blockId, drops, slab);
		} catch (Exception e) {
			// One malformed declaration should cost its own table, not every table after it in the pack.
			Obsidian.LOGGER.error("[Obsidian] Failed to build the loot table for {}", blockId, e);
			return 0;
		}
		if (table == null) return 0;

		table.randomSequence(key.identifier());
		pack.addLootTable(key.identifier(), table);
		return 1;
	}

	private static LootTable table(Identifier blockId, DropInformation drops, boolean slab) {
		boolean declared = drops != null && drops.drops != null && drops.drops.length > 0;

		if (!declared) {
			// Dropping itself only works if the block has an item to drop — "has_item": false blocks do not.
			if (!BuiltInRegistries.ITEM.containsKey(blockId)) return null;

			Pool pool = Pool.of().rolls(1).entry(slab ? slabEntry(blockId) : Entry.item(blockId));
			// A block that drops itself is the all-or-nothing case; one that drops a quantity carries
			// explosion decay on the entry instead, which thins the stack rather than losing it outright.
			if (!slab && (drops == null || drops.survivesExplosion)) pool.condition(Condition.survivesExplosion());
			return LootTable.block().pool(pool);
		}

		List<DropInformation.Drop> valid = new ArrayList<>();
		for (DropInformation.Drop drop : drops.drops) {
			if (drop != null && drop.name != null) valid.add(drop);
		}
		if (valid.isEmpty()) return null;

		// The ore idiom: Silk Touch takes the block, everything else rolls the declared drops. The first
		// child of an alternatives entry that passes its condition is the one used, so this is one pool —
		// and with more than one drop the alternatives roll for a single one of them, which is worth
		// saying out loud rather than quietly doing.
		if (drops.silkTouchDropsBlock && BuiltInRegistries.ITEM.containsKey(blockId)) {
			if (valid.size() > 1) {
				Obsidian.LOGGER.warn("[Obsidian] {} declares {} drops alongside \"silk_touch_drops_block\", which "
						+ "rolls for one of them rather than giving them all. Declare one drop, or ship the table "
						+ "yourself with \"loot_table\".", blockId, valid.size());
			}

			List<Entry> children = new ArrayList<>();
			children.add(Entry.item(blockId).condition(CAN_SILK_TOUCH));
			for (DropInformation.Drop drop : valid) children.add(dropEntry(blockId, drop, drops.survivesExplosion));

			Entry ore = Entry.alternatives(children.toArray(Entry[]::new));
			return LootTable.block().pool(Pool.of().rolls(1).entry(ore));
		}

		// Every drop that passes its own conditions is given, so each one needs a pool of its own — a
		// single pool holding several entries rolls for one of them rather than giving them all, which is
		// what makes a crop able to drop its produce and its seeds together.
		LootTable table = LootTable.block();
		for (DropInformation.Drop drop : valid) {
			table.pool(Pool.of().rolls(1).entry(dropEntry(blockId, drop, drops.survivesExplosion)));
		}
		return table;
	}

	/**
	 * One declared drop: how many, what an enchantment adds to that, and whether explosions thin it.
	 *
	 * <p>An entry carries a single condition, so a drop that is both conditioned on the block's state and
	 * on Silk Touch cannot have both — the state wins, and the clash is warned about.
	 */
	private static Entry dropEntry(Identifier blockId, DropInformation.Drop drop, boolean survivesExplosion) {
		Entry entry = Entry.item(drop.name);

		// A drop conditioned on the block's own state — the crop idiom, where produce comes only from a
		// grown crop while its seeds come from any stage.
		Map<String, Object> state = drop.stateWhen();
		if (state != null) {
			if (drop.dropsIfSilkTouch) {
				Obsidian.LOGGER.warn("[Obsidian] The drop of {} from {} declares both \"when\" and "
						+ "\"drops_if_silk_touch\"; an entry takes one condition, so only \"when\" is applied.",
						drop.name, blockId);
			}
			entry.condition(Condition.matchBlock(BlockPredicate.of()
					.blocks(blockId)
					.parameter("state", state)));
		} else if (drop.dropsIfSilkTouch) {
			entry.condition(CAN_SILK_TOUCH);
		}

		List<LootFunction> modifiers = new ArrayList<>();

		DropInformation.Count count = drop.getCount();
		if (count != null) {
			modifiers.add(LootFunction.setCount(count.fixed()
					? NumberProvider.constant(count.min())
					: NumberProvider.uniform(count.min(), count.max())));
		}

		DropInformation.Fortune fortune = drop.getFortune();
		if (fortune != null) modifiers.add(bonus(fortune));

		if (survivesExplosion) modifiers.add(LootFunction.explosionDecay());

		if (modifiers.size() == 1) {
			entry.function(modifiers.getFirst());
		} else if (modifiers.size() > 1) {
			entry.function(LootFunction.sequence(modifiers.toArray(LootFunction[]::new)));
		}
		return entry;
	}

	private static LootFunction bonus(DropInformation.Fortune fortune) {
		return switch (fortune.formula()) {
			case "uniform_bonus_count" ->
					LootFunction.applyBonusUniform(fortune.enchantment(), fortune.bonusMultiplier());
			case "binomial_with_bonus_count" ->
					LootFunction.applyBonusBinomial(fortune.enchantment(), fortune.extra(), fortune.probability());
			case "ore_drops" -> LootFunction.applyBonusOreDrops(fortune.enchantment());
			default -> throw new IllegalArgumentException("Unknown fortune formula \"" + fortune.formula()
					+ "\"; expected ore_drops, uniform_bonus_count or binomial_with_bonus_count");
		};
	}

	/** Vanilla's slab table: two items when the block broken was a double slab, one otherwise. */
	private static Entry slabEntry(Identifier blockId) {
		LootFunction doubled = LootFunction.setCount(NumberProvider.constant(2))
				.condition(Condition.matchBlock(BlockPredicate.of()
						.blocks(blockId)
						.state("type", "double")));
		return Entry.item(blockId).function(LootFunction.sequence(doubled, LootFunction.explosionDecay()));
	}

}
