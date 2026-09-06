package io.github.vampirestudios.obsidian.registry;

import io.github.vampirestudios.obsidian.Obsidian;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;

public class OMenus {
	public static final MenuType<ChestMenu> GENERIC_9x7 = register("generic_9x7", OMenus::sevenRows);
	public static final MenuType<ChestMenu> GENERIC_9x8 = register("generic_9x8", OMenus::eightRows);
	public static final MenuType<ChestMenu> GENERIC_9x9 = register("generic_9x9", OMenus::nineRows);

	public static ChestMenu sevenRows(int i, Inventory inventory) {
		return new ChestMenu(GENERIC_9x7, i, inventory, 7);
	}

	public static ChestMenu eightRows(int i, Inventory inventory) {
		return new ChestMenu(GENERIC_9x8, i, inventory, 8);
	}

	public static ChestMenu nineRows(int i, Inventory inventory) {
		return new ChestMenu(GENERIC_9x9, i, inventory, 9);
	}

	private static <T extends AbstractContainerMenu> MenuType<T> register(String string, MenuType.MenuSupplier<T> menuSupplier) {
		return Registry.register(BuiltInRegistries.MENU, Obsidian.id(string), new MenuType<>(menuSupplier, FeatureFlags.VANILLA_SET));
	}

	public static void init() {
	}
}
