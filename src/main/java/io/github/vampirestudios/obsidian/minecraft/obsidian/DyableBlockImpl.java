package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Functions;
import io.github.vampirestudios.obsidian.utils.ColorUtil;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.random.RandomGenerator;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DyableBlockImpl extends BlockWithEntity {

    public io.github.vampirestudios.obsidian.api.obsidian.block.Block block;

    public DyableBlockImpl(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Settings settings) {
        super(settings);
        this.block = block;
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return !block.information.translucent ? 0.2F : 1.0F;
    }

    @Override
    public boolean isShapeFullCube(BlockState state, BlockView world, BlockPos pos) {
        return !block.information.translucent;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return block.information.translucent;
    }

    @Override
    public ActionResult onUse(BlockState blockState_1, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult_1) {
        BlockEntity blockEntity = Objects.requireNonNull(world).getBlockEntity(pos);
        if (blockEntity instanceof DyableBlockEntity dyableBlockEntity) {
            if (!world.isClient) {
                if (player.getStackInHand(hand).getItem() instanceof DyeItem dyeItem) {
                    int newColor = ColorUtil.toIntRgb(dyeItem.getColor().getColorComponents());
                    dyableBlockEntity.markDirty();
                    dyableBlockEntity.setColorAndSync(newColor);
                    player.sendMessage(Text.literal("Dyed a block: " + newColor).formatted(Formatting.ITALIC), true);
                    return ActionResult.CONSUME;
                }
                System.out.println("Server Color: " + dyableBlockEntity.getDyeColor());
            } else {
                System.out.println("Client Color: " + dyableBlockEntity.getDyeColor());
            }
        }

        if (!world.isClient) {
            Item item = Registry.ITEM.get(block.functions.use.item);
            if (block.functions.use.functionType.equals(Functions.Function.FunctionType.REQUIRES_SHIFTING) && player.isSneaking() && block.functions.use.predicate.matches()) {
                Optional<CommandFunction> function = Objects.requireNonNull(world.getServer()).getCommandFunctionManager().getFunction(block.functions.use.function_file);
                function.ifPresent(commandFunction -> world.getServer().getCommandFunctionManager().execute(commandFunction, world.getServer().getCommandSource()));
                return ActionResult.SUCCESS;
            } else if (block.functions.use.functionType.equals(Functions.Function.FunctionType.REQUIRES_ITEM) && player.getMainHandStack().getItem().equals(item) && block.functions.use.predicate.matches()) {
                Optional<CommandFunction> function = Objects.requireNonNull(world.getServer()).getCommandFunctionManager().getFunction(block.functions.use.function_file);
                function.ifPresent(commandFunction -> world.getServer().getCommandFunctionManager().execute(commandFunction, world.getServer().getCommandSource()));
                return ActionResult.SUCCESS;
            } else if (block.functions.use.functionType.equals(Functions.Function.FunctionType.REQUIRES_SHIFTING_AND_ITEM) && player.isSneaking() && player.getMainHandStack().getItem().equals(item) && block.functions.use.predicate.matches()) {
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
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        var stack = new ItemStack(this);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof DyableBlockEntity dyableBlockEntity) {
            ((DyeableItem) stack.getItem()).setColor(stack, dyableBlockEntity.getDyeColor());
        }
        return stack;
    }

    @Override
    public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {
        if (block.display != null && block.display.lore.length != 0) {
            for (TooltipInformation tooltipInformation : block.display.lore) {
                tooltip.add(tooltipInformation.getTextType("tooltip"));
            }
        }
    }

    public int getColor(BlockState blockState, BlockRenderView blockRenderView, BlockPos blockPos, int tintIndex) {
        if (tintIndex == 0 && blockRenderView != null) {
            BlockEntity blockEntity = blockRenderView.getBlockEntity(blockPos);
            if (blockEntity instanceof DyableBlockEntity) {
                return ((DyableBlockEntity) blockEntity).getDyeColor();
            }
        }
        return 0;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DyableBlockEntity(this.block, pos, state);
    }

}