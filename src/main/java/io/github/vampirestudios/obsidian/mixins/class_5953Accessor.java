package io.github.vampirestudios.obsidian.mixins;

import com.google.common.collect.BiMap;
import net.minecraft.block.Block;
import net.minecraft.class_5953;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Supplier;

@Mixin(class_5953.class)
public interface class_5953Accessor {
	@Accessor
	static Supplier<BiMap<Block, Block>> getField_29560() {
		throw new UnsupportedOperationException();
	}

	@Mutable
	@Accessor
	static void setField_29560(Supplier<BiMap<Block, Block>> field_29560) {
		throw new UnsupportedOperationException();
	}

	@Accessor
	static Supplier<BiMap<Block, Block>> getField_29561() {
		throw new UnsupportedOperationException();
	}

	@Mutable
	@Accessor
	static void setField_29561(Supplier<BiMap<Block, Block>> field_29561) {
		throw new UnsupportedOperationException();
	}
}
