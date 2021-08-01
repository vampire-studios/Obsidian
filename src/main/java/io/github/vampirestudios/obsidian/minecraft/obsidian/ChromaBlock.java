//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ChromaBlock extends BlockWithEntity {
    private final Block block;

    public ChromaBlock(Block block, AbstractBlock.Settings settings) {
        super(settings);
        this.block = block;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChromaEntity(block, pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack stack = super.getPickStack(world, pos, state);
        if (stack.getItem() instanceof ChromaItem item) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof ChromaEntity chromaEntity) {
                item.setColor(stack, chromaEntity.getColor());
            }
        }
        return stack;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof ChromaEntity) {
            int hue = itemStack.getOrCreateSubNbt("display").getInt("color");
            if (hue != 0) {
                ((ChromaEntity)blockEntity).setColor(itemStack.getOrCreateSubNbt("display").getInt("color"));
            } else {
                ((ChromaEntity)blockEntity).setColor(16777215);
            }
        }

    }
}
