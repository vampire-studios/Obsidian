package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.api.EventActionHandler;
import io.github.vampirestudios.obsidian.api.InteractionContext;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.world.Pattern;
import io.github.vampirestudios.obsidian.minecraft.obsidian.PatternMatcher;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.AddonFormats;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.failedRegistering;
import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.register;

public class Patterns implements AddonModule {

	/** Registered once, the first time a pattern is loaded, rather than per definition. */
	private static boolean hooked = false;

	@Override
	public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException {
		Pattern pattern = AddonFormats.read(addon, file, Pattern.class);
		if (pattern == null) return;

		Identifier patternId = Identifier.fromNamespaceAndPath(id.modId(), AddonFormats.baseName(file));
		try {
			pattern.id = patternId;
			register(ContentRegistries.PATTERNS, "pattern", patternId, pattern);
			hookActivation();
		} catch (Exception e) {
			failedRegistering("pattern", file.getName(), e);
		}
	}

	/** One callback serves every pattern; it asks the registry which one the click could belong to. */
	private static void hookActivation() {
		if (hooked) return;
		hooked = true;

		UseBlockCallback.EVENT.register((player, level, hand, hit) -> {
			if (level.isClientSide()) return InteractionResult.PASS;

			ItemStack held = player.getItemInHand(hand);
			BlockPos clicked = hit.getBlockPos();

			for (Pattern pattern : ContentRegistries.PATTERNS) {
				if (!matchesActivator(pattern, held)) continue;

				PatternMatcher.Match match = PatternMatcher.find(level, clicked, pattern);
				if (match == null) continue;

				if (activate(level, player, held, pattern, match)) return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		});
	}

	/** No activator means an empty hand activates it; one means that item and nothing else. */
	private static boolean matchesActivator(Pattern pattern, ItemStack held) {
		if (pattern.activator == null) return held.isEmpty();

		Item activator = BuiltInRegistries.ITEM.getValue(pattern.activator);
		return activator != null && held.is(activator);
	}

	/**
	 * Pays for the shape and runs it.
	 *
	 * @return whether the activation happened, so the caller can stop looking
	 */
	private static boolean activate(Level level, Player player, ItemStack held, Pattern pattern,
	                                PatternMatcher.Match match) {
		InteractionContext ctx = InteractionContext.ofBlock(level, match.positions().isEmpty()
				? player.blockPosition() : match.positions().getFirst(), player, match.facing());

		if (!canPay(player, pattern)) {
			runEvents(pattern, "on_fail", ctx);
			// Consumed either way: the shape was built and clicked, it just could not be paid for.
			return true;
		}

		pay(player, pattern);
		consumePattern(level, pattern, match);

		if (pattern.consumeActivator && !player.getAbilities().instabuild) {
			held.shrink(1);
		}

		runEvents(pattern, "on_activate", ctx);
		return true;
	}

	private static boolean canPay(Player player, Pattern pattern) {
		Pattern.Cost cost = pattern.cost;
		if (cost == null) return true;
		if (player.getAbilities().instabuild && !cost.chargeCreative) return true;

		if (player.experienceLevel < cost.experienceLevels) return false;

		for (Pattern.Cost.Entry entry : cost.items) {
			if (entry.item == null) continue;
			Item item = BuiltInRegistries.ITEM.getValue(entry.item);
			if (item == null) return false;

			int found = ContainerHelper.clearOrCountMatchingItems(player.getInventory(),
					stack -> stack.is(item), entry.count(), true);
			if (found < entry.count()) return false;
		}
		return true;
	}

	private static void pay(Player player, Pattern pattern) {
		Pattern.Cost cost = pattern.cost;
		if (cost == null) return;
		if (player.getAbilities().instabuild && !cost.chargeCreative) return;

		if (cost.experienceLevels > 0) player.giveExperienceLevels(-cost.experienceLevels);

		for (Pattern.Cost.Entry entry : cost.items) {
			if (entry.item == null) continue;
			Item item = BuiltInRegistries.ITEM.getValue(entry.item);
			if (item == null) continue;

			ContainerHelper.clearOrCountMatchingItems(player.getInventory(),
					stack -> stack.is(item), entry.count(), false);
		}
	}

	/** Removes the shape, or turns it into {@code replace_with}. */
	private static void consumePattern(Level level, Pattern pattern, PatternMatcher.Match match) {
		if (!pattern.consumePattern) return;

		Block replacement = pattern.replaceWith == null
				? Blocks.AIR
				: BuiltInRegistries.BLOCK.getValue(pattern.replaceWith);
		BlockState state = (replacement == null ? Blocks.AIR : replacement).defaultBlockState();

		match.positions().forEach(pos -> level.setBlockAndUpdate(pos, state));
	}

	private static void runEvents(Pattern pattern, String event, InteractionContext ctx) {
		for (Map<String, Object> actionConfig : pattern.getEventActions(event)) {
			EventActionHandler.dispatch(ctx, (String) actionConfig.get("action"), actionConfig, event);
		}
	}

	@Override
	public String getType() {
		return "world/pattern";
	}
}
