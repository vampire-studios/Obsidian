package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;
import java.util.Locale;

public class ArmorItemImpl extends ArmorItem {

    public io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem item;

    public ArmorItemImpl(ArmorMaterial material, io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem item, Settings settings) {
        super(material, ArmorItem.Type.valueOf(item.armorType.toUpperCase(Locale.ROOT)), settings);
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
        return item.information.name.getTranslation("item");
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if (item.display != null && item.display.lore.length != 0) {
            for (TooltipInformation tooltipInformation : item.display.lore) {
                tooltip.add(tooltipInformation.getTextType("tooltip"));
            }
        }
    }

}
