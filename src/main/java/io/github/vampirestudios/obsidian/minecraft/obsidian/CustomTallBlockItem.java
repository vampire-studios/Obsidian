package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CustomTallBlockItem extends BlockItem {

    public final Block block;

    public CustomTallBlockItem(Block block, net.minecraft.block.Block blockImpl, Settings settings) {
        super(blockImpl, settings);
        this.block = block;
    }

    protected boolean place(ItemPlacementContext itemPlacementContext, BlockState blockState) {
        World world = itemPlacementContext.getWorld();
        BlockPos blockPos = itemPlacementContext.getBlockPos().up();
        BlockState blockState2 = world.isWater(blockPos) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
        world.setBlockState(blockPos, blockState2, 27);
        return super.place(itemPlacementContext, blockState);
    }

    @Override
    public ItemStack getDefaultStack() {
        ItemStack stack = super.getDefaultStack();
        block.information.getRemovedTooltipSections().forEach(stack::addHideFlag);
        return stack;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return block.information.itemProperties.has_glint;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return block.information.itemProperties.is_enchantable;
    }

    @Override
    public int getEnchantability() {
        return block.information.itemProperties.enchantability;
    }

    @Override
    public Text getName() {
        return block.information.name.getName("block");
    }

}
