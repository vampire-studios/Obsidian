package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

public class HorizontalFacingDyableBlockImpl extends HorizontalFacingBlockImpl implements EntityBlock {
    private final Identifier id;

    public HorizontalFacingDyableBlockImpl(Identifier id, io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Properties settings) {
        super(block, settings);
        this.id = id;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DyeableBlockEntity(id, pos, state);
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean bl) {
        ItemStack stack = super.getCloneItemStack(world, pos, state, bl);
        if (stack.getItem() instanceof CustomDyeableItem) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof DyeableBlockEntity dyeableBlockEntity) {
                stack.set(DataComponents.DYED_COLOR, new DyedItemColor(dyeableBlockEntity.getDyeColor()));
            }
        }
        return stack;
    }

    @Override
    @NullMarked
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DyeableBlockEntity dyeableBlockEntity) {
            dyeableBlockEntity.setDyeColor(DyedItemColor.getOrDefault(itemStack, block.additional_information.defaultColor));
        }

    }
}