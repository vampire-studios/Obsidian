package io.github.vampirestudios.obsidian.api.obsidian.item;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.Identifier;

public class ShieldItem extends Item {

	@SerializedName("can_have_banner")
	public boolean canHaveBanner = true;
	@SerializedName("cooldown_ticks")
	public int cooldownTicks;
	@SerializedName("repair_item")
	public Identifier repairItem = Identifier.withDefaultNamespace("air");
	@SerializedName("block_sound")
	public Identifier blockSound = Identifier.withDefaultNamespace("item.shield.block");
	@SerializedName("break_sound")
	public Identifier breakSound = Identifier.withDefaultNamespace("item.shield.break");

}
