package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TridentEntity.class)
public interface TridentEntityAccessor {
	@Accessor("tridentStack")
	ItemStack getTridentStack();

	@Accessor("tridentStack")
	void setTridentStack(ItemStack tridentStack);
}