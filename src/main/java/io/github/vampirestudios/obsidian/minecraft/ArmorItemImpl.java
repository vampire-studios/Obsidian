package io.github.vampirestudios.obsidian.minecraft;

import io.github.vampirestudios.obsidian.api.TooltipInformation;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class ArmorItemImpl extends ArmorItem {

    public io.github.vampirestudios.obsidian.api.item.ArmorItem item;

    public ArmorItemImpl(ArmorMaterial material, io.github.vampirestudios.obsidian.api.item.ArmorItem item, Settings settings) {
        super(material, EquipmentSlot.byName(item.armorSlot), settings);
        this.item = item;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return item.information.has_glint;
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if (item.display != null && item.display.lore.length != 0) {
            for (TooltipInformation tooltipInformation : item.display.lore) {
                tooltip.add(tooltipInformation.getTextType());
            }
        }
    }

}
