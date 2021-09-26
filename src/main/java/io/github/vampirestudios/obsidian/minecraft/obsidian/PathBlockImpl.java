package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.block.DirtPathBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.world.BlockView;

import java.util.List;
import java.util.Map;

public class PathBlockImpl extends DirtPathBlock {

    public Block block;

    public PathBlockImpl(Settings settings, Block block) {
        super(settings);
        this.block = block;
    }

    @Override
    public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {
        if (block.display != null && block.display.lore.length != 0) {
            for (TooltipInformation tooltipInformation : block.display.lore) {
                tooltip.add(tooltipInformation.getTextType("tooltip"));
            }
        }
    }

}