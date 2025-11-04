package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.CosmeticSlotExt;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractContainerMenu.class)
public class ScreenHandlerMixin implements CosmeticSlotExt {
    @Unique
    public ItemStack headCosmetics = ItemStack.EMPTY;
    @Unique
    public ItemStack chestCosmetics = ItemStack.EMPTY;
    @Unique
    public ItemStack leggingsCosmetics = ItemStack.EMPTY;
    @Unique
    public ItemStack bootsCosmetics = ItemStack.EMPTY;

    public void setHeadCosmetics (ItemStack itemStack) {
        headCosmetics = itemStack;
    }
    public ItemStack getHeadCosmetics () {
        return headCosmetics;
    }

    @Override
    public ItemStack getChestCosmetics() {
        return chestCosmetics;
    }

    @Override
    public void setChestCosmetics(ItemStack itemStack) {
        this.chestCosmetics = itemStack;
    }

    @Override
    public ItemStack getLeggingsCosmetics() {
        return leggingsCosmetics;
    }

    @Override
    public void setLeggingsCosmetics(ItemStack itemStack) {
        this.leggingsCosmetics = itemStack;
    }

    @Override
    public ItemStack getBootsCosmetics() {
        return bootsCosmetics;
    }

    @Override
    public void setBootsCosmetics(ItemStack itemStack) {
        this.bootsCosmetics = itemStack;
    }
}