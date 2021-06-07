package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.ShieldItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShieldItemImpl extends net.minecraft.item.ShieldItem {

    public ShieldItem shieldItem;

    public ShieldItemImpl(ShieldItem shieldItem, Item.Settings settings) {
        super(settings);
        this.shieldItem = shieldItem;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return shieldItem.information.has_glint;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return shieldItem.information.is_enchantable;
    }

    @Override
    public int getEnchantability() {
        return shieldItem.information.enchantability;
    }

    @Override
    public Text getName() {
        return shieldItem.information.name.getName("item");
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (shieldItem.display != null && shieldItem.display.lore.length != 0) {
            for (TooltipInformation tooltipInformation : shieldItem.display.lore) {
                tooltip.add(tooltipInformation.getTextType("tooltip"));
            }
        }
    }
}
