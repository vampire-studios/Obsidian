package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class SittableAndDyableBlock extends DyeableBlock {

    public static final BooleanProperty OCCUPIED = BooleanProperty.create("occupied");

    public SittableAndDyableBlock(Block block, Properties settings) {
        super(block, settings);
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return block.information.blockProperties != null ? !block.information.blockProperties.translucent ? 0.2F : 1.0F : super.getShadeBrightness(state, world, pos);
    }

    @Override
    public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return block.information.blockProperties != null ? !block.information.blockProperties.translucent : super.isCollisionShapeFullBlock(state, world, pos);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return block.information.blockProperties != null ? block.information.blockProperties.translucent : super.propagatesSkylightDown(state, world, pos);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if(state.getValue(OCCUPIED) || !world.getBlockState(pos.above()).isAir() || player.isPassenger())
            return super.use(state, world, pos, player, hand, hit);

        if(!world.isClientSide) {
            SeatEntity entity = new SeatEntity(world);
            entity.setPosRaw(pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5);

            world.addFreshEntity(entity);
            player.startRiding(entity);

            world.setBlockAndUpdate(pos, state.setValue(OCCUPIED, true));
        }

        return InteractionResult.SUCCESS;
    }

}
