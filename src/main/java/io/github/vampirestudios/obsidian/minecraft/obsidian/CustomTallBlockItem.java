package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CustomTallBlockItem extends BlockItem {

    public final Block block;

    public CustomTallBlockItem(Block block, net.minecraft.world.level.block.Block blockImpl, Properties settings) {
        super(blockImpl, settings);
        this.block = block;
    }

    protected boolean placeBlock(BlockPlaceContext itemPlacementContext, BlockState blockState) {
        Level world = itemPlacementContext.getLevel();
        BlockPos blockPos = itemPlacementContext.getClickedPos().above();
        BlockState blockState2 = world.isWaterAt(blockPos) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
        world.setBlock(blockPos, blockState2, 27);
        return super.placeBlock(itemPlacementContext, blockState);
    }

    @Override
    public ItemStack getDefaultInstance() {
        ItemStack stack = super.getDefaultInstance();
        block.information.getRemovedTooltipSections().forEach(stack::hideTooltipPart);
        return stack;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return block.information.getItemSettings().hasEnchantmentGlint;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return block.information.getItemSettings().isEnchantable;
    }

    @Override
    public int getEnchantmentValue() {
        return block.information.getItemSettings().enchantability;
    }

    @Override
    public Component getDescription() {
        return block.information.name.getName("block");
    }

}
