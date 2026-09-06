package io.github.vampirestudios.obsidian.minecraft;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.ui.GUI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class DynamicContainer extends AbstractContainerMenu {
	private Container inventory;
	private final Player player;
	private GUI gui;

	public DynamicContainer(int id, Inventory playerInventory) {
		super(Obsidian.DYNAMIC_CONTAINER, id); // Use an appropriate MenuType or define your own.
		this.player = playerInventory.player;
		this.inventory = new SimpleContainer(9); // Example size, adjust as needed
	}

	public DynamicContainer setGui(GUI gui) {
		this.gui = gui;
		return this;
	}

	public DynamicContainer setContainerInventory(Container inventory) {
		this.inventory = inventory;
		return this;
	}

	public void setupSlots() {
		for (GUI.Slot slot : gui.slots) {
			switch (slot.type) {
				case "filtered":
					Predicate<ItemStack> filter = stack -> BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(slot.filter);
					addSlot(new FilteredSlot(inventory, slots.size(), slot.x, slot.y, filter));
					break;
				case "standard":
					addSlot(new Slot(inventory, slots.size(), slot.x, slot.y));
					break;
			}
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return true; // Implement actual check
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index < 9) {
				if (!this.moveItemStackTo(itemstack1, 9, this.slots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(itemstack1, 0, 9, false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}

			slot.onTake(player, itemstack1);
		}
		return itemstack;
	}
}