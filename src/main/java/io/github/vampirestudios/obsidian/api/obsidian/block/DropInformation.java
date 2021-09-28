package io.github.vampirestudios.obsidian.api.obsidian.block;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.Identifier;

public class DropInformation {
	public Identifier[] drops;
	@SerializedName("requires_silk_touch")
	public boolean requiresSilkTouch = false;
}