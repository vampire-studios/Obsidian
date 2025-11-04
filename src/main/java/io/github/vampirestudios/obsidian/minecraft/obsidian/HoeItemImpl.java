package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;

public class HoeItemImpl extends HoeItem {

    public ToolItem item;

    public HoeItemImpl(ToolItem item, ToolMaterial material, Properties settings) {
        super(material, 1, 1, settings);
        this.item = item;
    }

//    @Override
//    public boolean canBeDepleted() {
//        return item.damageable;
//    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return item.information.getItemSettings().hasEnchantmentGlint.orElse(stack.isEnchanted());
    }
}