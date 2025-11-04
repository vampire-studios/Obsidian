package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpyglassItem;

public class SpyglassItemImpl extends SpyglassItem {

    public io.github.vampirestudios.obsidian.api.obsidian.item.Item item;

    public SpyglassItemImpl(io.github.vampirestudios.obsidian.api.obsidian.item.Item item, Properties settings) {
        super(settings);
        this.item = item;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return item.information.getItemSettings().hasEnchantmentGlint.orElse(stack.isEnchanted());
    }
}
