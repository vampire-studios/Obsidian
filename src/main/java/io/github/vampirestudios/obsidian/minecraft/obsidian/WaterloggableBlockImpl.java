package io.github.vampirestudios.obsidian.minecraft.obsidian;

import com.mojang.serialization.MapCodec;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class WaterloggableBlockImpl extends Block implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public io.github.vampirestudios.obsidian.api.obsidian.block.Block block;
    private static final MapCodec<Block> CODEC = simpleCodec(WaterloggableBlockImpl::new);

    @Override
    public MapCodec<? extends Block> codec() {
        return CODEC;
    }

    public WaterloggableBlockImpl(Properties settings) {
        super(settings);
        this.block = null;
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    public WaterloggableBlockImpl(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Properties settings) {
        super(settings);
        this.block = block;
        this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
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

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        LevelAccessor worldAccess = ctx.getLevel();
        BlockPos blockPos = ctx.getClickedPos();
        boolean bl = worldAccess.getFluidState(blockPos).getType() == Fluids.WATER;
        return this.defaultBlockState().setValue(WATERLOGGED, bl);
    }

    /*public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }*/

    /*@Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return block.information.light_absorption_value;
    }*/

//    @Override
//    public InteractionResult use(BlockState blockState_1, Level world, BlockPos blockPos_1, Player playerEntity_1, InteractionHand hand_1, BlockHitResult blockHitResult_1) {
//        if (!world.isClientSide) {
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