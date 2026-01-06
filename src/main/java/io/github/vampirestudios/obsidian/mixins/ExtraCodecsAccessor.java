package io.github.vampirestudios.obsidian.mixins;

import com.mojang.serialization.Codec;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Function;

@org.spongepowered.asm.mixin.Mixin(net.minecraft.util.ExtraCodecs.class)
public interface ExtraCodecsAccessor {
	@Invoker
	static Codec<Float> callFloatRangeMinInclusiveWithMessage(float f, float g, Function<Float, String> function) {
		throw new UnsupportedOperationException();
	}
}
