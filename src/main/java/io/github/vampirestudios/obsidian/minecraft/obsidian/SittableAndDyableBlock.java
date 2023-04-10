package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SittableAndDyableBlock extends DyeableBlock {

    public static final BooleanProperty OCCUPIED = BooleanProperty.of("occupied");

    public SittableAndDyableBlock(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return !block.information.blockProperties.translucent ? 0.2F : 1.0F;
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return !block.information.blockProperties.translucent;
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return block.information.blockProperties.translucent;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(state.get(OCCUPIED) || !world.getBlockState(pos.up()).isAir() || player.hasVehicle())
            return super.onUse(state, world, pos, player, hand, hit);

        if(!world.isClient) {
            SeatEntity entity = new SeatEntity(world);
            entity.setPos(pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5);

            world.spawnEntity(entity);
            player.startRiding(entity);

            world.setBlockState(pos, state.with(OCCUPIED, true));
        }

        return ActionResult.SUCCESS;
    }

}
