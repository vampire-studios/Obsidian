package io.github.vampirestudios.obsidian.minecraft;

import io.github.vampirestudios.obsidian.api.TooltipInformation;
import io.github.vampirestudios.obsidian.api.item.Item;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class AxeItemImpl extends AxeItem {

    public Item item;

    public AxeItemImpl(Item item, ToolMaterial material, int attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
        this.item = item;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return item.information.has_glint;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return item.information.is_enchantable;
    }

    @Override
    public int getEnchantability() {
        return item.information.enchantability;
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