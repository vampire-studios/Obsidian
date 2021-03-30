package io.github.vampirestudios.obsidian.api.obsidian.entity.components;

import com.google.gson.annotations.SerializedName;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import net.minecraft.util.Identifier;

import java.util.List;

public class AgeableComponent extends Component {

    @SerializedName("drop_items")
    public List<Identifier> dropItems;
    public float duration;
    @SerializedName("feed_items")
    public List<Identifier> feedItems;
    @SerializedName("grow_up")
    public String growUp;

}