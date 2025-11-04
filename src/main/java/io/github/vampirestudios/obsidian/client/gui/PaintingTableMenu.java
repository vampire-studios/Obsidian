package io.github.vampirestudios.obsidian.client.gui;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;

public class PaintingTableMenu extends ChestMenu {
	protected PaintingTableMenu(int containerId, Inventory playerInventory) {
		super(MenuType.GENERIC_9x5, containerId, playerInventory, new SimpleContainer(9 * 5), 5);
	}
}
