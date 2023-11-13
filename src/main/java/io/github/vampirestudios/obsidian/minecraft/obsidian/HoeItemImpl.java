package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class HoeItemImpl extends HoeItem {

    public ToolItem item;

    public HoeItemImpl(ToolItem item, Tier material, int attackDamage, float attackSpeed, Properties settings) {
        super(material, attackDamage, attackSpeed, settings);
        this.item = item;
    }

    @Override
    public boolean canBeDepleted() {
        return item.damageable;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return item.information.getItemSettings().hasEnchantmentGlint;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return item.information.getItemSettings().isEnchantable;
    }

    @Override
    public int getEnchantmentValue() {
        return item.information.getItemSettings().enchantability;
    }

    @Override
    public Component getDescription() {
        return item.information.name.getName("item");
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
        if (item.lore != null) {
            for (TooltipInformation tooltipInformation : item.lore) {
                tooltip.add(tooltipInformation.getTextType("tooltip"));
            }
        }
    }

}