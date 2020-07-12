package io.github.vampirestudios.obsidian.minecraft;

import io.github.vampirestudios.obsidian.api.TooltipInformation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

import static net.minecraft.block.TntBlock.primeTnt;

public class BlockImpl extends Block {

    public io.github.vampirestudios.obsidian.api.block.Block block;

    public BlockImpl(io.github.vampirestudios.obsidian.api.block.Block block, Settings settings) {
        super(settings);
        this.block = block;
    }

    @Override
    public ActionResult onUse(BlockState blockState_1, World world_1, BlockPos blockPos_1, PlayerEntity playerEntity_1, Hand hand_1, BlockHitResult blockHitResult_1) {
        if(block.information.action.equals("explode")) {
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
        }
        return ActionResult.FAIL;
    }

    @Override
    public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {
        if (block.display != null && block.display.lore.length != 0) {
            for (TooltipInformation tooltipInformation : block.display.lore) {
                tooltip.add(tooltipInformation.getTextType(tooltipInformation.text));
            }
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext entityContext_1) {
        return Block.createCuboidShape(
                block.information.boundingBoxes[0],
                block.information.boundingBoxes[1],
                block.information.boundingBoxes[2],
                block.information.boundingBoxes[3],
                block.information.boundingBoxes[4],
                block.information.boundingBoxes[5]
        );
    }

    @Override
    public VoxelShape getRayTraceShape(BlockState state, BlockView world, BlockPos pos) {
        return Block.createCuboidShape(
                block.information.boundingBoxes[0],
                block.information.boundingBoxes[1],
                block.information.boundingBoxes[2],
                block.information.boundingBoxes[3],
                block.information.boundingBoxes[4],
                block.information.boundingBoxes[5]
        );
    }
}