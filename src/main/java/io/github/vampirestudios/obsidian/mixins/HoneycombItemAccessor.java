package io.github.vampirestudios.obsidian.mixins;

import com.google.common.collect.BiMap;
import net.minecraft.block.Block;
import net.minecraft.item.HoneycombItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Supplier;

@Mixin(HoneycombItem.class)
public interface HoneycombItemAccessor {
	@Accessor
	static Supplier<BiMap<Block, Block>> getWAXED_BLOCKS() {
		throw new UnsupportedOperationException();
	}

	@Mutable
	@Accessor
	static void setWAXED_BLOCKS(Supplier<BiMap<Block, Block>> WAXED_BLOCKS) {
		throw new UnsupportedOperationException();
	}

	@Accessor
	static Supplier<BiMap<Block, Block>> getUNWAXED_BLOCKS() {
		throw new UnsupportedOperationException();
	}

	@Mutable
	@Accessor
	static void setUNWAXED_BLOCKS(Supplier<BiMap<Block, Block>> UNWAXED_BLOCKS) {
		throw new UnsupportedOperationException();
	}
}
