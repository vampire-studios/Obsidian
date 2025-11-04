package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.function.Consumer;

public class DyeableArmorItemImpl extends Item {

    public io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem item;

    public DyeableArmorItemImpl(ArmorMaterial material, io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem item, Properties settings) {
        super(settings
                .humanoidArmor(material, ArmorType.valueOf(item.armorType))
                .component(DataComponents.DYED_COLOR, new DyedItemColor(item.information.getItemSettings().defaultColor)));
        this.item = item;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return item.information.getItemSettings().hasEnchantmentGlint.orElse(stack.isEnchanted());
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        item.addLore(consumer);
    }

}
