package io.github.vampirestudios.obsidian.api.obsidian.block;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;

public class DropInformation {
	public Drop[] drops;
	@SerializedName("survives_explosion")
	public boolean survivesExplosion = false;
	@SerializedName("xp_drop_amount")
	public int xpDropAmount = 1;

	public static class Drop {
		public ResourceLocation name;
		@SerializedName("drops_if_silk_touch")
		public boolean dropsIfSilkTouch = false;
	}
}