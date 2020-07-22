package io.github.vampirestudios.obsidian.minecraft;

import io.github.vampirestudios.obsidian.api.TooltipInformation;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class ItemImpl extends Item {

    public io.github.vampirestudios.obsidian.api.item.Item item;

    public ItemImpl(io.github.vampirestudios.obsidian.api.item.Item item, Settings settings) {
        super(settings);
        this.item = item;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return item.information.enchanted;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if (item.display != null && item.display.lore.length != 0) {
            for (TooltipInformation tooltipInformation : item.display.lore) {
                tooltip.add(tooltipInformation.getTextType(tooltipInformation.text));
            }
        }
    }

}
