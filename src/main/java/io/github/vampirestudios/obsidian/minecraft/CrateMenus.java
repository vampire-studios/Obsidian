package io.github.vampirestudios.obsidian.minecraft;

import eu.pb4.placeholders.api.parsers.TagParser;
import io.github.vampirestudios.obsidian.api.obsidian.menu.CustomMenuConfig;
import io.github.vampirestudios.obsidian.registry.OMenus;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Prediction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Opens the menus described by a {@link CustomMenuConfig}, including rolling its loot pool.
 *
 * <p>Kept separate from the item that currently uses it so that anything holding a config can open the
 * same menu — a crate block, an event action, an entity interaction — without going through an item.
 */
public final class CrateMenus {

	private static final Logger LOGGER = LogManager.getLogger();

	private CrateMenus() {
	}

	/**
	 * Opens {@code config}'s menu for the player, rolling its loot pool if it has one. Does nothing on the
	 * client, so callers may call it from either side.
	 *
	 * @param consumable the stack to shrink when the pool asks to consume one — the crate item, or the key
	 *                   that opened a crate block. Pass {@link ItemStack#EMPTY} when nothing is consumed.
	 */
	public static void open(Player player, CustomMenuConfig config, ItemStack consumable) {
		if (config == null || player.level().isClientSide()) return;

		int rows = config.getRows();
		MenuType<ChestMenu> menuType = menuTypeFor(rows);
		Component title = TagParser.QUICK_TEXT.parseNode(config.getTitle()).toComponent();

		if (!config.hasLoot()) {
			player.openMenu(new SimpleMenuProvider((syncId, inv, _) -> menuType.create(syncId, inv), title));
			return;
		}

		CustomMenuConfig.LootPool pool = config.lootPool;
		List<ItemStack> rewards = pool.roll(player.level().getRandom());

		if (pool.consume && !consumable.isEmpty() && !player.getAbilities().instabuild) {
			consumable.shrink(1);
		}

		if (pool.getMode() == CustomMenuConfig.LootPool.Mode.INSTANT) {
			for (ItemStack reward : rewards) giveOrDrop(player, reward);
			return;
		}

		openPreview(player, config, menuType, title, rows, rewards);
	}

	private static void openPreview(Player player, CustomMenuConfig config, MenuType<ChestMenu> menuType,
									Component title, int rows, List<ItemStack> rewards) {
		SimpleContainer contents = new SimpleContainer(rows * 9);
		if (rewards.size() > contents.getContainerSize()) {
			LOGGER.warn("Crate \"{}\" rolled {} rewards but its menu only has {} slots; the rest are discarded.",
					config.getTitle(), rewards.size(), contents.getContainerSize());
		}
		for (int i = 0; i < rewards.size() && i < contents.getContainerSize(); i++) {
			contents.setItem(i, rewards.get(i));
		}
		player.openMenu(new SimpleMenuProvider(
				(syncId, inv, _) -> new CrateMenu(menuType, syncId, inv, contents, rows), title));
	}

	/** Puts the stack in the player's inventory, dropping it at their feet if there is no room. */
	public static void giveOrDrop(Player player, ItemStack stack) {
		if (stack.isEmpty()) return;
		if (!player.addItem(stack)) player.drop(stack, false, Prediction.PREDICTED);
	}

	/** The chest menu type for a row count. Rows 7–9 are the types this mod registers; the rest are vanilla. */
	public static MenuType<ChestMenu> menuTypeFor(int rows) {
		return switch (rows) {
			case 1 -> MenuType.GENERIC_9x1;
			case 2 -> MenuType.GENERIC_9x2;
			case 4 -> MenuType.GENERIC_9x4;
			case 5 -> MenuType.GENERIC_9x5;
			case 6 -> MenuType.GENERIC_9x6;
			case 7 -> OMenus.GENERIC_9x7;
			case 8 -> OMenus.GENERIC_9x8;
			case 9 -> OMenus.GENERIC_9x9;
			default -> MenuType.GENERIC_9x3;
		};
	}
}
