package io.github.vampirestudios.obsidian.api.obsidian.item;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.Identifier;

public class ShieldItem extends Item {

	@SerializedName("shield_base")
	public Identifier shieldBase;
	@SerializedName("shield_base_no_pattern")
	public Identifier shieldBaseNoPattern;

}
