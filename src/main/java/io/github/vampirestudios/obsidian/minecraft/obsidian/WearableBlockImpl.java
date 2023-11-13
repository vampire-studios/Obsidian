package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Functions;
import net.minecraft.commands.CommandFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class WearableBlockImpl extends Block {

    public io.github.vampirestudios.obsidian.api.obsidian.block.Block block;

    public WearableBlockImpl(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Properties settings) {
        super(settings);
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
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return block.information.getBlockSettings() != null ? block.information.getBlockSettings().translucent : super.propagatesSkylightDown(state, world, pos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
    }

    @Override
    public InteractionResult use(BlockState blockState_1, Level world, BlockPos blockPos_1, Player playerEntity_1, InteractionHand hand_1, BlockHitResult blockHitResult_1) {
        if (!world.isClientSide) {
            Item item = BuiltInRegistries.ITEM.get(block.functions.use.item);
            if (block.functions.use.functionType.equals(Functions.Function.FunctionType.REQUIRES_SHIFTING) && playerEntity_1.isShiftKeyDown() && block.functions.use.predicate.matches()) {
                Optional<CommandFunction> function = Objects.requireNonNull(world.getServer()).getFunctions().get(block.functions.use.function_file);
                function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
                return InteractionResult.SUCCESS;
            } else if (block.functions.use.functionType.equals(Functions.Function.FunctionType.REQUIRES_ITEM) && playerEntity_1.getMainHandItem().getItem().equals(item) && block.functions.use.predicate.matches()) {
                Optional<CommandFunction> function = Objects.requireNonNull(world.getServer()).getFunctions().get(block.functions.use.function_file);
                function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
                return InteractionResult.SUCCESS;
            } else if (block.functions.use.functionType.equals(Functions.Function.FunctionType.REQUIRES_SHIFTING_AND_ITEM) && playerEntity_1.isShiftKeyDown() && playerEntity_1.getMainHandItem().getItem().equals(item) && block.functions.use.predicate.matches()) {
                Optional<CommandFunction> function = Objects.requireNonNull(world.getServer()).getFunctions().get(block.functions.use.function_file);
                function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
                return InteractionResult.SUCCESS;
            } else if (block.functions.use.functionType.equals(Functions.Function.FunctionType.NONE) &&  block.functions.use.predicate.matches()) {
                Optional<CommandFunction> function = Objects.requireNonNull(world.getServer()).getFunctions().get(block.functions.use.function_file);
                function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (block.functions.scheduled_tick.predicate.matches()) {
            Optional<CommandFunction> function = world.getServer().getFunctions().get(block.functions.scheduled_tick.function_file);
            function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (block.functions.random_tick.predicate.matches()) {
            Optional<CommandFunction> function = world.getServer().getFunctions().get(block.functions.random_tick.function_file);
            function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (!world.isClientSide && block.functions.random_display_tick.predicate.matches()) {
            Optional<CommandFunction> function = Objects.requireNonNull(world.getServer()).getFunctions().get(block.functions.random_display_tick.function_file);
            function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        if (block.rendering != null && block.lore.length != 0) {
            for (TooltipInformation tooltipInformation : block.lore) {
                tooltip.add(tooltipInformation.getTextType("tooltip"));
            }
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        float[] boundingBox = block.information.collisionShape.full_shape;
        return box(boundingBox[0], boundingBox[1], boundingBox[2], boundingBox[3], boundingBox[4], boundingBox[5]);
    }
}