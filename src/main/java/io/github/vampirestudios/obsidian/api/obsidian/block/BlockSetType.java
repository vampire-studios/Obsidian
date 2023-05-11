package io.github.vampirestudios.obsidian.api.obsidian.block;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;

public class BlockSetType {

    public ResourceLocation id;

    @SerializedName("can_open_by_hand")
    @blue.endless.jankson.annotation.SerializedName("can_open_by_hand")
    public boolean canOpenByHand;

    @SerializedName("sound_type")
    @blue.endless.jankson.annotation.SerializedName("sound_type")
    public ResourceLocation soundType;

    @SerializedName("door_close")
    @blue.endless.jankson.annotation.SerializedName("door_close")
    public ResourceLocation doorClose;

    @SerializedName("door_open")
    @blue.endless.jankson.annotation.SerializedName("door_open")
    public ResourceLocation doorOpen;

    @SerializedName("trapdoor_close")
    @blue.endless.jankson.annotation.SerializedName("trapdoor_close")
    public ResourceLocation trapdoorClose;

    @SerializedName("trapdoor_open")
    @blue.endless.jankson.annotation.SerializedName("trapdoor_open")
    public ResourceLocation trapdoorOpen;

    @SerializedName("pressure_plate_click_off")
    @blue.endless.jankson.annotation.SerializedName("pressure_plate_click_off")
    public ResourceLocation pressurePlateClickOff;

    @SerializedName("pressure_plate_click_on")
    @blue.endless.jankson.annotation.SerializedName("pressure_plate_click_on")
    public ResourceLocation pressurePlateClickOn;

    @SerializedName("button_click_off")
    @blue.endless.jankson.annotation.SerializedName("button_click_off")
    public ResourceLocation buttonClickOff;

    @SerializedName("button_click_on")
    @blue.endless.jankson.annotation.SerializedName("button_click_on")
    public ResourceLocation buttonClickOn;
}
