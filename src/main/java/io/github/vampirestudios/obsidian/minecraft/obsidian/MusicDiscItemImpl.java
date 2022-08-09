package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.MusicDisc;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.List;

public class MusicDiscItemImpl extends MusicDiscItem {
    private final MusicDisc musicDisc;

    public MusicDiscItemImpl(MusicDisc musicDisc, Settings settings) {
        super(musicDisc.comparator_output, Registry.SOUND_EVENT.get(musicDisc.music), settings, musicDisc.length);
        this.musicDisc = musicDisc;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return musicDisc.information.hasEnchantmentGlint;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return musicDisc.information.isEnchantable;
    }

    @Override
    public int getEnchantability() {
        return musicDisc.information.enchantability;
    }

    @Override
    public Text getName() {
        return musicDisc.information.name.getName("item");
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if (musicDisc.display != null && musicDisc.display.lore.length != 0) {
            for (TooltipInformation tooltipInformation : musicDisc.display.lore) {
                tooltip.add(tooltipInformation.getTextType("tooltip"));
            }
        }
    }

}
