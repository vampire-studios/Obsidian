package io.github.vampirestudios.obsidian.api.obsidian.item;

import blue.endless.jankson.annotation.SerializedName;
import net.minecraft.resources.Identifier;

public class ShieldItem extends Item {

    @SerializedName("can_have_banner")
    @com.google.gson.annotations.SerializedName("can_have_banner")
    public boolean canHaveBanner = true;
    @SerializedName("cooldown_ticks")
    @com.google.gson.annotations.SerializedName("cooldown_ticks")
    public int cooldownTicks;
    @SerializedName("repair_item")
    @com.google.gson.annotations.SerializedName("repair_item")
    public Identifier repairItem = Identifier.withDefaultNamespace("air");
    @SerializedName("block_sound")
    @com.google.gson.annotations.SerializedName("block_sound")
    public Identifier blockSound = Identifier.withDefaultNamespace("item.shield.block");
    @SerializedName("break_sound")
    @com.google.gson.annotations.SerializedName("break_sound")
    public Identifier breakSound = Identifier.withDefaultNamespace("item.shield.break");

}
