package io.github.vampirestudios.obsidian.api.obsidian.entity.components;

import com.google.gson.annotations.SerializedName;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;

public class AdmireItemComponent extends Component {

    @SerializedName("cooldown_after_being_attacked")
    public int cooldownAfterBeingAttacked;
    public int duration;

}