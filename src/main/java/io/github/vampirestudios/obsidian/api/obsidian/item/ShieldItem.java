package io.github.vampirestudios.obsidian.api.obsidian.item;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;

public class ShieldItem extends Item {

    @SerializedName("shield_base")
    public ResourceLocation shieldBase;
    @SerializedName("shield_base_no_pattern")
    public ResourceLocation shieldBaseNoPattern;
    public boolean can_have_banner = true;
    public int cooldownTicks;
    public ResourceLocation repairItem = new ResourceLocation("air");

}
