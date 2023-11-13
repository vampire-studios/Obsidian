package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Locale;

public class ArmorItemImpl extends ArmorItem {

    public io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem item;

    public ArmorItemImpl(ArmorMaterial material, io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem item, Properties settings) {
        super(material, ArmorItem.Type.valueOf(item.armorType.toUpperCase(Locale.ROOT)), settings);
        this.item = item;
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
        return item.information.name.getTranslation("item");
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
