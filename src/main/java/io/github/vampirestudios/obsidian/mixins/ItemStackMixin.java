package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.IForgeItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IForgeItemStack {

	@Shadow public abstract boolean hasNbt();

	@Shadow private @Nullable NbtCompound nbt;

	@Shadow public abstract Item getItem();

	/**
	 * @author Olivia
	 * @reason idk
	 */
	@Overwrite
	private int getHideFlags() {
		return this.hasNbt() && this.nbt.contains("HideFlags", 99) ? this.nbt.getInt("HideFlags") : this.getItem().getDefaultTooltipHideFlags((ItemStack) (Object) this);
	}

}
