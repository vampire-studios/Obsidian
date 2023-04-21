package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Block.class)
public class BlockMixin {

	/**
	 * @author OliviaTheVampire
	 */
	@Overwrite
	public String toString() {
		return this.getClass().toString() + "{" + BuiltInRegistries.BLOCK.getKey((Block)(Object)this) + "}";
	}
}
