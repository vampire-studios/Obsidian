package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.api.obsidian.block.Functions;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CakeBlockImpl extends CakeBaseBlock {

    public io.github.vampirestudios.obsidian.api.obsidian.block.Block block;

    public CakeBlockImpl(Block block) {
        super(block.information.cake_slices);
        this.block = block;
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
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return block.information.blockProperties.translucent;
    }

    @Override
    public ActionResult onUse(BlockState blockState_1, World world, BlockPos blockPos_1, PlayerEntity playerEntity_1, Hand hand_1, BlockHitResult blockHitResult_1) {
        if (!world.isClient) {
            Item item = Registry.ITEM.get(block.functions.use.item);
            if (block.functions.use.functionType.equals(Functions.Function.FunctionType.REQUIRES_SHIFTING) && playerEntity_1.isSneaking() && block.functions.use.predicate.matches()) {
                Optional<CommandFunction> function = Objects.requireNonNull(world.getServer()).getCommandFunctionManager().getFunction(block.functions.use.function_file);
                function.ifPresent(commandFunction -> world.getServer().getCommandFunctionManager().execute(commandFunction, world.getServer().getCommandSource()));
                return ActionResult.SUCCESS;
            } else if (block.functions.use.functionType.equals(Functions.Function.FunctionType.REQUIRES_ITEM) && playerEntity_1.getMainHandStack().getItem().equals(item) && block.functions.use.predicate.matches()) {
                Optional<CommandFunction> function = Objects.requireNonNull(world.getServer()).getCommandFunctionManager().getFunction(block.functions.use.function_file);
                function.ifPresent(commandFunction -> world.getServer().getCommandFunctionManager().execute(commandFunction, world.getServer().getCommandSource()));
                return ActionResult.SUCCESS;
            } else if (block.functions.use.functionType.equals(Functions.Function.FunctionType.REQUIRES_SHIFTING_AND_ITEM) && playerEntity_1.isSneaking() && playerEntity_1.getMainHandStack().getItem().equals(item) && block.functions.use.predicate.matches()) {
                Optional<CommandFunction> function = Objects.requireNonNull(world.getServer()).getCommandFunctionManager().getFunction(block.functions.use.function_file);
                function.ifPresent(commandFunction -> world.getServer().getCommandFunctionManager().execute(commandFunction, world.getServer().getCommandSource()));
                return ActionResult.SUCCESS;
            } else if (block.functions.use.functionType.equals(Functions.Function.FunctionType.NONE) &&  block.functions.use.predicate.matches()) {
                Optional<CommandFunction> function = Objects.requireNonNull(world.getServer()).getCommandFunctionManager().getFunction(block.functions.use.function_file);
                function.ifPresent(commandFunction -> world.getServer().getCommandFunctionManager().execute(commandFunction, world.getServer().getCommandSource()));
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.FAIL;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
        if (block.functions.scheduled_tick.predicate.matches()) {
            Optional<CommandFunction> function = world.getServer().getCommandFunctionManager().getFunction(block.functions.scheduled_tick.function_file);
            function.ifPresent(commandFunction -> world.getServer().getCommandFunctionManager().execute(commandFunction, world.getServer().getCommandSource()));
        }
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random) {
        if (block.functions.random_tick.predicate.matches()) {
            Optional<CommandFunction> function = world.getServer().getCommandFunctionManager().getFunction(block.functions.random_tick.function_file);
            function.ifPresent(commandFunction -> world.getServer().getCommandFunctionManager().execute(commandFunction, world.getServer().getCommandSource()));
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, RandomGenerator random) {
        if (!world.isClient && block.functions.random_display_tick.predicate.matches()) {
            Optional<CommandFunction> function = Objects.requireNonNull(world.getServer()).getCommandFunctionManager().getFunction(block.functions.random_display_tick.function_file);
            function.ifPresent(commandFunction -> world.getServer().getCommandFunctionManager().execute(commandFunction, world.getServer().getCommandSource()));
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {
        if (block.rendering != null && block.lore.length != 0) {
            for (TooltipInformation tooltipInformation : block.lore) {
                tooltip.add(tooltipInformation.getTextType("tooltip"));
            }
        }
    }

}