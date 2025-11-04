package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ToolMaterial;

public class ShovelItemImpl extends ShovelItem {

    public ToolItem item;

    public ShovelItemImpl(ToolItem item, ToolMaterial material, Properties settings) {
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