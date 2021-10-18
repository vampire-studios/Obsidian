package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class BlockImpl extends Block {

    public io.github.vampirestudios.obsidian.api.obsidian.block.Block block;

    public BlockImpl(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Settings settings) {
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
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
    }

    @Override
    public ActionResult onUse(BlockState blockState_1, World world_1, BlockPos blockPos_1, PlayerEntity playerEntity_1, Hand hand_1, BlockHitResult blockHitResult_1) {
		/*if (block.information.action.equals("explode")) {
			ItemStack itemStack_1 = playerEntity_1.getStackInHand(hand_1);
			Item item_1 = itemStack_1.getItem();
			if (item_1 != Items.FLINT_AND_STEEL && item_1 != Items.FIRE_CHARGE) {
				return super.onUse(blockState_1, world_1, blockPos_1, playerEntity_1, hand_1, blockHitResult_1);
			} else {
				primeTnt(world_1, blockPos_1);
				world_1.setBlockState(blockPos_1, Blocks.AIR.getDefaultState(), 11);
				if (item_1 == Items.FLINT_AND_STEEL) {
					itemStack_1.damage(1, playerEntity_1, playerEntity -> playerEntity.sendToolBreakStatus(hand_1));
				} else {
					itemStack_1.decrement(1);
				}

				return ActionResult.SUCCESS;
			}
		}*/
        /*if (!world_1.isClient && block.functions.on_use != null) {
            if (world_1.getServer().getCommandFunctionManager().getFunction(block.functions.on_use).isPresent()) {
                world_1.getServer().getCommandFunctionManager().execute(world_1.getServer().getCommandFunctionManager().getFunction(block.functions.on_use).get(), world_1.getServer().getCommandSource());
                return ActionResult.SUCCESS;
            }
        }*/
        return ActionResult.FAIL;
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
    public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {
        if (block.display != null && block.display.lore.length != 0) {
            for (TooltipInformation tooltipInformation : block.display.lore) {
                tooltip.add(tooltipInformation.getTextType("tooltip"));
            }
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        float[] boundingBox = block.information.bounding_box;
        return createCuboidShape(boundingBox[0], boundingBox[1], boundingBox[2], boundingBox[3], boundingBox[4], boundingBox[5]);
    }
}