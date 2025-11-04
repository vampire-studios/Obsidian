package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;
import java.util.Optional;

public class CakeBlockImpl extends CakeBaseBlock {

    public io.github.vampirestudios.obsidian.api.obsidian.block.Block block;

    public CakeBlockImpl(Block block) {
        super(block.information.cake_slices);
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
    public boolean propagatesSkylightDown(BlockState state) {
        return block.information.getBlockSettings() != null ? block.information.getBlockSettings().translucent : super.propagatesSkylightDown(state);
    }

//    @Override
//    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
//        if (!level.isClientSide) {
//            Item item = BuiltInRegistries.ITEM.get(block.functions.use.item);
//            if (block.functions.use.functionType.equals(Functions.Function.FunctionType.REQUIRES_SHIFTING) && playerEntity_1.isShiftKeyDown() && block.functions.use.predicate.matches()) {
//                Optional<CommandFunction<CommandSourceStack>> function = Objects.requireNonNull(world.getServer()).getFunctions().get(block.functions.use.function_file);
//                function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
//                return InteractionResult.SUCCESS;
//            } else if (block.functions.use.functionType.equals(Functions.Function.FunctionType.REQUIRES_ITEM) && playerEntity_1.getMainHandItem().getItem().equals(item) && block.functions.use.predicate.matches()) {
//                Optional<CommandFunction<CommandSourceStack>> function = Objects.requireNonNull(world.getServer()).getFunctions().get(block.functions.use.function_file);
//                function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
//                return InteractionResult.SUCCESS;
//            } else if (block.functions.use.functionType.equals(Functions.Function.FunctionType.REQUIRES_SHIFTING_AND_ITEM) && playerEntity_1.isShiftKeyDown() && playerEntity_1.getMainHandItem().getItem().equals(item) && block.functions.use.predicate.matches()) {
//                Optional<CommandFunction<CommandSourceStack>> function = Objects.requireNonNull(world.getServer()).getFunctions().get(block.functions.use.function_file);
//                function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
//                return InteractionResult.SUCCESS;
//            } else if (block.functions.use.functionType.equals(Functions.Function.FunctionType.NONE) &&  block.functions.use.predicate.matches()) {
//                Optional<CommandFunction<CommandSourceStack>> function = Objects.requireNonNull(world.getServer()).getFunctions().get(block.functions.use.function_file);
//                function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
//                return InteractionResult.SUCCESS;
//            }
//        }
//        return InteractionResult.FAIL;
//    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (block.functions.scheduled_tick.predicate.matches()) {
            Optional<CommandFunction<CommandSourceStack>> function = world.getServer().getFunctions().get(block.functions.scheduled_tick.function_file);
            function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (block.functions.random_tick.predicate.matches()) {
            Optional<CommandFunction<CommandSourceStack>> function = world.getServer().getFunctions().get(block.functions.random_tick.function_file);
            function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (!world.isClientSide && block.functions.random_display_tick.predicate.matches()) {
            Optional<CommandFunction<CommandSourceStack>> function = Objects.requireNonNull(world.getServer()).getFunctions().get(block.functions.random_display_tick.function_file);
            function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
        }
    }

}