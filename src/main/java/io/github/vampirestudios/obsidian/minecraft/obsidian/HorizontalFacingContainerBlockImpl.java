package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.IContainerProvider;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * The facing counterpart of {@link ContainerBlockImpl} — chests, barrels and most storage furniture
 * want to face the player who placed them.
 */
public class HorizontalFacingContainerBlockImpl extends HorizontalFacingBlockImpl
		implements EntityBlock, IContainerProvider {

	private final Identifier blockId;

	public HorizontalFacingContainerBlockImpl(Identifier blockId,
	                                          io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
	                                          Properties settings) {
		super(block, settings);
		this.blockId = blockId;
	}

	@Override
	public Block.Behaviour.@Nullable Container getContainer() {
		return ContainerLogic.containerOf(this.block);
	}

	@Override
	public Block getDefinition() {
		return this.block;
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ContainerBlockEntity(this.blockId, pos, state);
	}

	@Override
	@NullMarked
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		InteractionResult handled = super.useWithoutItem(state, level, pos, player, hit);
		if (handled != InteractionResult.PASS) return handled;

		return ContainerLogic.open(level, pos, state, player);
	}
}
