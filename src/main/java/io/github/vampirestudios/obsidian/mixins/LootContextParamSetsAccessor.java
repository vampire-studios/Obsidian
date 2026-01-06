package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.util.context.ContextKeySet;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Consumer;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.world.level.storage.loot.parameters.LootContextParamSets.class)
public interface LootContextParamSetsAccessor {
	@Invoker
	static ContextKeySet callRegister(String string, Consumer<ContextKeySet.Builder> consumer) {
		throw new UnsupportedOperationException();
	}
}
