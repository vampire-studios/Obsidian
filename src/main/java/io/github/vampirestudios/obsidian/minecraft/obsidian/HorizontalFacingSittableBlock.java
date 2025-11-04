package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class HorizontalFacingSittableBlock extends HorizontalFacingBlockImpl {
    public static final BooleanProperty OCCUPIED = BlockStateProperties.OCCUPIED;

    public HorizontalFacingSittableBlock(Block block, Properties settings) {
        super(block, settings);
        this.registerDefaultState(this.defaultBlockState().setValue(OCCUPIED, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            Entity entity = null;
            List<SeatEntity> entities = level.getEntities(Obsidian.SEAT, new AABB(pos), chair -> true);
            if(entities.isEmpty()) {
                entity = Obsidian.SEAT.spawn((ServerLevel) level, pos, EntitySpawnReason.TRIGGERED);
            } else {
                entity = entities.getFirst();
            }

            player.startRiding(entity);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(OCCUPIED));
    }

}
