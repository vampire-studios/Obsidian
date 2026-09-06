package io.github.vampirestudios.obsidian.api.obsidian.item;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.Identifier;

public class FoodPotionEffect {

	public Identifier effect;

	/** Defaults to certain — an effect the pack bothered to declare should land unless it says otherwise. */
	public float chance = 1.0F;
	public int duration;
	public int amplifier;
	@SerializedName("show_particles")
	public boolean showParticles = true;
	@SerializedName("show_icon")
	public boolean showIcon = true;
	public boolean visible = false;
	public boolean ambient = false;

}