package io.github.vampirestudios.obsidian.api.obsidian.item;

import com.google.gson.annotations.SerializedName;
import net.minecraft.util.Identifier;

public class ShieldItem extends Item {

    @SerializedName("shield_base")
    public Identifier shieldBase;
    @SerializedName("shield_base_no_pattern")
    public Identifier shieldBaseNoPattern;
    public boolean can_have_banner = true;
    public int cooldownTicks;
    public Identifier repairItem = new Identifier("air");

}
