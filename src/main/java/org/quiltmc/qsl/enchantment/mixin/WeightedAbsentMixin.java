package org.quiltmc.qsl.enchantment.mixin;

import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import org.quiltmc.qsl.enchantment.mixinterface.MutableWeight;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WeightedEntry.IntrusiveBase.class)
public class WeightedAbsentMixin implements MutableWeight {
	@Shadow
	@Final
	@Mutable
	private Weight weight;

	public void setWeight(Weight weight) {
		this.weight = weight;
	}
}