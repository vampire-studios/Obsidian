package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class HorizontalFacingSittableBlock extends HorizontalFacingBlockImpl {

    public static final BooleanProperty OCCUPIED = BooleanProperty.create("occupied");

    public HorizontalFacingSittableBlock(Block block, Properties settings) {
        super(block, settings);
        this.registerDefaultState(this.defaultBlockState().setValue(OCCUPIED, false).setValue(FACING, Direction.NORTH));
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

        world.setBlockAndUpdate(pos, state.setValue(OCCUPIED, false));

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(OCCUPIED));
    }

}
