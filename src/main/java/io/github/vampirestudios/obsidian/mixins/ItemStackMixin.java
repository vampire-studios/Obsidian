package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.api.IForgeItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements IForgeItemStack {

	@Shadow public abstract boolean hasTag();

	@Shadow private @Nullable CompoundTag tag;

	@Shadow public abstract Item getItem();

	/**
	 * @author Olivia
	 * @reason idk
	 */
	@Overwrite
	private int getHideFlags() {
		return this.hasTag() && this.tag.contains("HideFlags", 99) ? this.tag.getInt("HideFlags") : this.getItem().getDefaultTooltipHideFlags((ItemStack) (Object) this);
	}

}
