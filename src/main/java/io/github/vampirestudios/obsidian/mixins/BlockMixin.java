package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Block.class)
public class BlockMixin {

	/**
	 * @author OliviaTheVampire
	 * @reason For more information about block
	 */
	@Overwrite
	public String toString() {
		return this.getClass().toString() + "{" + Registry.BLOCK.getId((Block)(Object)this) + "}";
	}
}
