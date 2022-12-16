package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Block.class)
public class BlockMixin {

	/**
	 * @author OliviaTheVampire
	 */
	@Overwrite
	public String toString() {
		return this.getClass().toString() + "{" + Registries.BLOCK.getId((Block)(Object)this) + "}";
	}
}
