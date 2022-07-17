package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.block.MapColor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MapColor.class)
public class MapColorMixin {

	@Shadow @Final public int color;

	/**
	 * @author OliviaTheVampire
	 */
	@Overwrite
	public int getRenderColor(MapColor.Brightness brightness) {
		if ((Object)this == MapColor.CLEAR) {
			return 0;
		} else {
			int i = brightness.brightness;
			int j = (this.color >> 16 & 255) * i / 255;
			int k = (this.color >> 8 & 255) * i / 255;
			int l = (this.color & 255) * i / 255;
			return -16777216 | l << 16 | k << 8 | j;
		}
	}
}
