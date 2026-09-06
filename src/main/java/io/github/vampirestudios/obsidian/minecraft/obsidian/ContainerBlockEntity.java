package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.IContainerProvider;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * The storage behind a block that declares {@code behaviour.container}.
 *
 * <p>Its size and title come from the block it belongs to, so no state is duplicated here beyond the
 * slots themselves and the viewer count that {@code purge} needs.
 */
public class ContainerBlockEntity extends BaseContainerBlockEntity {

	private final Block definition;
	private final Block.Behaviour.Container container;
	private NonNullList<ItemStack> items;

	/** Live viewers, so {@code purge} can tell the last one closing from a lid merely changing hands. */
	private int viewers;

	public ContainerBlockEntity(Identifier id, BlockPos pos, BlockState state) {
		super(BuiltInRegistries.BLOCK_ENTITY_TYPE.getValue(Utils.appendToPath(id, "_be")), pos, state);

		IContainerProvider provider = state.getBlock() instanceof IContainerProvider p ? p : null;
		this.definition = provider == null ? null : provider.getDefinition();
		this.container = provider == null ? null : provider.getContainer();
		this.items = NonNullList.withSize(ContainerLogic.slotCount(this.container), ItemStack.EMPTY);
	}

	@Override
	public int getContainerSize() {
		return this.items.size();
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.items;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		this.items = items;
	}

	@Override
	protected Component getDefaultName() {
		return this.definition == null ? Component.empty() : ContainerLogic.title(this.definition, this.container);
	}

	@Override
	protected AbstractContainerMenu createMenu(int syncId, Inventory inventory) {
		return ContainerLogic.createMenu(syncId, inventory, this);
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		// Sized from the block, not the save: a pack that grows its container keeps the old contents.
		this.items = NonNullList.withSize(ContainerLogic.slotCount(this.container), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(input, this.items);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		ContainerHelper.saveAllItems(output, this.items);
	}

	@Override
	public void startOpen(ContainerUser user) {
		if (this.isRemoved()) return;
		this.viewers++;
	}

	@Override
	public void stopOpen(ContainerUser user) {
		if (this.isRemoved()) return;
		this.viewers = Math.max(0, this.viewers - 1);

		if (this.viewers == 0 && this.container != null && this.container.purge) {
			this.clearContent();
			this.setChanged();
		}
	}

	/**
	 * Vanilla's {@code preRemoveSideEffects} scatters the contents of any block entity that is a
	 * {@link net.minecraft.world.Container}, so {@code drop_contents: false} is expressed by not
	 * calling it rather than by dropping anything ourselves.
	 */
	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		if (shouldDropContents()) super.preRemoveSideEffects(pos, state);
	}

	/** Whether breaking this block should scatter its contents. */
	public boolean shouldDropContents() {
		return this.container == null || this.container.dropContents;
	}
}
