package io.github.vampirestudios.obsidian.minecraft;

import io.github.vampirestudios.obsidian.api.obsidian.ArmorProvider;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.List;

public class ArmorItemImpl extends ArmorItem implements ArmorProvider {

    public io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem item;

    public ArmorItemImpl(ArmorMaterial material, io.github.vampirestudios.obsidian.api.obsidian.item.ArmorItem item, Settings settings) {
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

    @Override
    public BipedEntityModel<LivingEntity> getArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, BipedEntityModel<LivingEntity> defaultModel) {
        return defaultModel;
    }

    @Override
    public String getArmorTexture(LivingEntity entity, ItemStack stack, EquipmentSlot slot, String defaultTexture) {
        return item.material.texture;
    }

}
