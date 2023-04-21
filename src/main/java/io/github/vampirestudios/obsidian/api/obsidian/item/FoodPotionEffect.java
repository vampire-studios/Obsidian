package io.github.vampirestudios.obsidian.api.obsidian.item;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;

public class FoodPotionEffect {

    public ResourceLocation effect;
    public float chance;
    public int duration;
    public int amplifier;
    @SerializedName("show_particles") public boolean showParticles = true;
    @SerializedName("show_icon") public boolean showIcon = true;
    public boolean visible = false;
    public boolean ambient = false;

}