package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.Item;
import net.fabricmc.fabric.api.item.v1.elytra.FabricElytraExtensions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;

public class ElytraItemImpl extends ElytraItem implements FabricElytraExtensions {

    private final Item item;

    public ElytraItemImpl(Item item, Properties settings) {
        super(settings);
        this.item = item;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return item.information.hasEnchantmentGlint;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return item.information.isEnchantable;
    }

    @Override
    public int getEnchantmentValue() {
        return item.information.enchantability;
    }

    @Override
    public Component getDescription() {
        return item.information.name.getName("item");
    }

}
