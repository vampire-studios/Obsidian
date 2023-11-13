package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.MusicDisc;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class MusicDiscItemImpl extends RecordItem {
    private final MusicDisc musicDisc;

    public MusicDiscItemImpl(MusicDisc musicDisc, Properties settings) {
        super(musicDisc.comparator_output, BuiltInRegistries.SOUND_EVENT.get(musicDisc.music), settings, musicDisc.length);
        this.musicDisc = musicDisc;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return musicDisc.information.getItemSettings().hasEnchantmentGlint;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return musicDisc.information.getItemSettings().isEnchantable;
    }

    @Override
    public int getEnchantmentValue() {
        return musicDisc.information.getItemSettings().enchantability;
    }

    @Override
    public Component getDescription() {
        return musicDisc.information.name.getName("item");
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
        if (musicDisc.lore != null) {
            for (TooltipInformation tooltipInformation : musicDisc.lore) {
                tooltip.add(tooltipInformation.getTextType("tooltip"));
            }
        }
    }

}
