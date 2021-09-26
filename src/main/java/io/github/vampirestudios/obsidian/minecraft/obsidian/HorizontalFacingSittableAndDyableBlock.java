package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HorizontalFacingSittableAndDyableBlock extends HorizontalFacingDyableBlockImpl {

    public static final BooleanProperty OCCUPIED = BooleanProperty.of("occupied");

    public HorizontalFacingSittableAndDyableBlock(Block block, Settings settings) {
        super(block, settings);
        this.setDefaultState(this.getDefaultState().with(OCCUPIED, false).with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<net.minecraft.block.Block, BlockState> builder) {
        super.appendProperties(builder.add(OCCUPIED));
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
