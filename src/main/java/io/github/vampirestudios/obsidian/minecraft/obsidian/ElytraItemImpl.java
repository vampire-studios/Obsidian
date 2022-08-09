package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.Item;
import net.fabricmc.fabric.api.item.v1.elytra.FabricElytraExtensions;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ElytraItemImpl extends ElytraItem implements FabricElytraExtensions {

    private final Item item;

    public ElytraItemImpl(Item item, Settings settings) {
        super(settings);
        this.item = item;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return item.information.hasEnchantmentGlint;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return item.information.isEnchantable;
    }

    @Override
    public int getEnchantability() {
        return item.information.enchantability;
    }

    @Override
    public Text getName() {
        return item.information.name.getName("item");
    }

}
