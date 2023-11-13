package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class PathBlockImpl extends DirtPathBlock {

    public Block block;

    public PathBlockImpl(Properties settings, Block block) {
        super(settings);
        this.block = block;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return block.information.getBlockSettings() != null ? !block.information.getBlockSettings().translucent ? 0.2F : 1.0F : super.getShadeBrightness(state, world, pos);
    }

    @Override
    public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return block.information.getBlockSettings() != null ? !block.information.getBlockSettings().translucent : super.isCollisionShapeFullBlock(state, world, pos);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return block.information.getBlockSettings() != null ? block.information.getBlockSettings().translucent : super.propagatesSkylightDown(state, world, pos);
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        if (block.rendering != null && block.lore.length != 0) {
            for (TooltipInformation tooltipInformation : block.lore) {
                tooltip.add(tooltipInformation.getTextType("tooltip"));
            }
        }
    }

}