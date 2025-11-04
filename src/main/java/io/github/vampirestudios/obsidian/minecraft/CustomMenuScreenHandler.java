/*
package io.github.vampirestudios.obsidian.minecraft;

import io.github.vampirestudios.obsidian.api.obsidian.item.CustomMenuConfig;
import io.github.vampirestudios.obsidian.registry.OItemComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CustomMenuScreenHandler extends AbstractContainerMenu {
    private final Container menuInventory;

    public CustomMenuScreenHandler(CustomMenuConfig customMenuConfig, int syncId, Inventory playerInventory) {
        super(MyMod.CUSTOM_MENU_SCREEN_HANDLER, syncId);
        int size = customMenuConfig.rows * 9;
        this.menuInventory = new SimpleContainer(size);

        // Add custom menu slots
        for (int i = 0; i < size; i++) {
            int x = 8 + (i % 9) * 18;
            int y = 18 + (i / 9) * 18;
            addSlot(new Slot(menuInventory, i, x, y));
        }
        // Optionally add player inventory slots here
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        // Save inventory to the item's NBT
        ItemStack stack = player.getMainHandItem();
        CustomMenuComponent comp = stack.get(OItemComponents.CUSTOM_MENU);
        if (comp != null) {
            menuInventory.forEach(comp.items::add);
        }
    }
}
*/
