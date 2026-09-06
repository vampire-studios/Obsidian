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
import org.jspecify.annotations.Nullable;

/**
 * A {@link BlockImpl} that stores items, built only for blocks declaring {@code behaviour.container}.
 *
 * <p>It exists as its own class so that {@code BlockImpl} does not have to implement {@link EntityBlock}
 * — every ordinary Obsidian block would otherwise be asked for a block entity on every placement.
 */
public class ContainerBlockImpl extends BlockImpl implements EntityBlock, IContainerProvider {

	private final Identifier blockId;

	public ContainerBlockImpl(Identifier blockId, io.github.vampirestudios.obsidian.api.obsidian.block.Block block,
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
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		// Seats and events first: a container that is also a seat keeps the interaction it already had,
		// and sneaking falls through to the container so both stay reachable.
		InteractionResult handled = super.useWithoutItem(state, level, pos, player, hit);
		if (handled != InteractionResult.PASS) return handled;

		return ContainerLogic.open(level, pos, state, player);
	}

	// Contents are scattered by ContainerBlockEntity.preRemoveSideEffects, which vanilla already calls
	// for every Container block entity — there is nothing to do on removal here.
}
