package io.github.vampirestudios.obsidian.api.obsidian.block;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.Identifier;

public class BlockSetType {

    public Identifier id;

    @SerializedName("can_open_by_hand")
    @blue.endless.jankson.annotation.SerializedName("can_open_by_hand")
    public boolean canOpenByHand;

    @SerializedName("can_open_by_wind_charge")
    @blue.endless.jankson.annotation.SerializedName("can_open_by_wind_charge")
    public boolean canOpenByWindCharge;

    @SerializedName("can_button_be_activated_by_arrows")
    @blue.endless.jankson.annotation.SerializedName("can_button_be_activated_by_arrows")
    public boolean canButtonBeActivatedByArrows;

    @SerializedName("pressure_plate_sensitivity")
    @blue.endless.jankson.annotation.SerializedName("pressure_plate_sensitivity")
    public String pressurePlateSensitivity;

    @SerializedName("sound_type")
    @blue.endless.jankson.annotation.SerializedName("sound_type")
    public Identifier soundType;

    @SerializedName("door_close")
    @blue.endless.jankson.annotation.SerializedName("door_close")
    public Identifier doorClose;

    @SerializedName("door_open")
    @blue.endless.jankson.annotation.SerializedName("door_open")
    public Identifier doorOpen;

    @SerializedName("trapdoor_close")
    @blue.endless.jankson.annotation.SerializedName("trapdoor_close")
    public Identifier trapdoorClose;

    @SerializedName("trapdoor_open")
    @blue.endless.jankson.annotation.SerializedName("trapdoor_open")
    public Identifier trapdoorOpen;

    @SerializedName("pressure_plate_click_off")
    @blue.endless.jankson.annotation.SerializedName("pressure_plate_click_off")
    public Identifier pressurePlateClickOff;

    @SerializedName("pressure_plate_click_on")
    @blue.endless.jankson.annotation.SerializedName("pressure_plate_click_on")
    public Identifier pressurePlateClickOn;

    @SerializedName("button_click_off")
    @blue.endless.jankson.annotation.SerializedName("button_click_off")
    public Identifier buttonClickOff;

    @SerializedName("button_click_on")
    @blue.endless.jankson.annotation.SerializedName("button_click_on")
    public Identifier buttonClickOn;

    public net.minecraft.world.level.block.state.properties.BlockSetType.PressurePlateSensitivity getPressurePlateSensitivity() {
        return net.minecraft.world.level.block.state.properties.BlockSetType.PressurePlateSensitivity.valueOf(pressurePlateSensitivity);
    }
}
