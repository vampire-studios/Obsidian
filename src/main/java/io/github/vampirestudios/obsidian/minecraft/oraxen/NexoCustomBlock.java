package io.github.vampirestudios.obsidian.minecraft.oraxen;

import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/** A Nexo custom block with the clickAction mechanic attached. */
public class NexoCustomBlock extends Block {
	private final NexoItem.Mechanics.CustomBlock mechanic;

	public NexoCustomBlock(NexoItem.Mechanics.CustomBlock mechanic, BlockBehaviour.Properties properties) {
		super(properties);
		this.mechanic = mechanic;
	}

	@Override
	protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
	                                      Player player, InteractionHand hand, BlockHitResult hitResult) {
		if (NexoClickActions.run(level, pos, player, mechanic != null ? mechanic.clickActions : null)) {
			return InteractionResult.SUCCESS;
		}
		return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
	                                           Player player, BlockHitResult hitResult) {
		if (NexoClickActions.run(level, pos, player, mechanic != null ? mechanic.clickActions : null)) {
			return InteractionResult.SUCCESS;
		}
		return super.useWithoutItem(state, level, pos, player, hitResult);
	}
}
