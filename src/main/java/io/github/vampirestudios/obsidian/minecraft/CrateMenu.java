package io.github.vampirestudios.obsidian.minecraft;

import net.minecraft.util.Prediction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;

/**
 * A chest menu showing the rewards a crate item rolled. Every click is rejected, so the menu is purely a
 * display: the contents are handed to the player once, when the menu closes.
 *
 * <p>It is built on the vanilla chest menu types, so the client renders it with the ordinary chest screen
 * and needs no registration of its own — the client's menu is a plain {@link ChestMenu} and only the
 * server holds this class.
 */
public class CrateMenu extends ChestMenu {

	private final SimpleContainer contents;
	private boolean granted;

	public CrateMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, SimpleContainer contents, int rows) {
		super(menuType, containerId, playerInventory, contents, rows);
		this.contents = contents;
	}

	@Override
	public void clicked(int slotIndex, int buttonNum, ContainerInput containerInput, Player player) {
		if (!player.level().isClientSide()) this.sendAllDataToRemote();
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
		return false;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		grantRewards(player);
	}

	/** Hands the rolled rewards over, once. Anything that does not fit is dropped at the player's feet. */
	private void grantRewards(Player player) {
		if (granted || player.level().isClientSide()) return;
		granted = true;

		for (int i = 0; i < contents.getContainerSize(); i++) {
			ItemStack stack = contents.removeItemNoUpdate(i);
			if (stack.isEmpty()) continue;
			if (!player.addItem(stack)) player.drop(stack, false, Prediction.PREDICTED);
		}
	}
}
