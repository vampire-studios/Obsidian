package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.utils.ColorUtil;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class DyableBlockImpl extends BlockWithEntity {

    public io.github.vampirestudios.obsidian.api.obsidian.block.Block block;

    public DyableBlockImpl(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Settings settings) {
        super(settings);
        this.block = block;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getServer().getCommandFunctionManager().getFunction(block.functions.scheduled_tick).isPresent()) {
            world.getServer().getCommandFunctionManager().execute(world.getServer().getCommandFunctionManager().getFunction(block.functions.scheduled_tick).get(), world.getServer().getCommandSource());
        }
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getServer().getCommandFunctionManager().getFunction(block.functions.random_tick).isPresent()) {
            world.getServer().getCommandFunctionManager().execute(world.getServer().getCommandFunctionManager().getFunction(block.functions.random_tick).get(), world.getServer().getCommandSource());
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (!world.isClient) {
            if (Objects.requireNonNull(world.getServer()).getCommandFunctionManager().getFunction(block.functions.random_display_tick).isPresent()) {
                Objects.requireNonNull(world.getServer()).getCommandFunctionManager().execute(world.getServer().getCommandFunctionManager().getFunction(block.functions.random_display_tick).get(), world.getServer().getCommandSource());
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = Objects.requireNonNull(world).getBlockEntity(pos);
        if (blockEntity instanceof DyableBlockEntity) {
            DyableBlockEntity dyableBlockEntity = (DyableBlockEntity) blockEntity;
            if (!world.isClient) {
                if (player.getStackInHand(hand).getItem() instanceof DyeItem) {
                    DyeItem dyeItem = (DyeItem) player.getStackInHand(hand).getItem();
                    int newColor = ColorUtil.toIntRgb(dyeItem.getColor().getColorComponents());
                    dyableBlockEntity.markDirty();
                    dyableBlockEntity.setColorAndSync(newColor);
                    player.sendMessage(new LiteralText("Dyed a block: " + newColor).formatted(Formatting.ITALIC), true);
                    return ActionResult.CONSUME;
                }
                System.out.println("Server Color: " + dyableBlockEntity.getDyeColor());
            } else {
                System.out.println("Client Color: " + dyableBlockEntity.getDyeColor());
            }
        }

        return ActionResult.FAIL;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return new ItemStack(this);
    }

    @Override
    public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {
        if (block.display != null && block.display.lore.length != 0) {
            for (TooltipInformation tooltipInformation : block.display.lore) {
                tooltip.add(tooltipInformation.getTextType("tooltip"));
            }
        }
    }

    /*@Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        ItemStack dropStack = new ItemStack(this);
        int destruction = state.get(dy);
        dropStack.getOrCreateTag().putInt(BaseAnvilItem.DESTRUCTION, destruction);
        return Lists.newArrayList(dropStack);
    }*/

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
    public BlockEntity createBlockEntity(BlockView world) {
        return new DyableBlockEntity(this.block);
    }

}