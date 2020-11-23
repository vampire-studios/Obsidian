package io.github.vampirestudios.obsidian.minecraft;

import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
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
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.List;

import static net.minecraft.block.TntBlock.primeTnt;

public class FenceImpl extends FenceBlock {

    public Block block;

    public FenceImpl(Block block) {
        super(Settings.of(block.information.getMaterial()));
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
        if (block.display.lore.length != 0) {
            for (TooltipInformation tooltipInformation : block.display.lore) {
                tooltip.add(tooltipInformation.getTextType());
            }
        }
    }

}