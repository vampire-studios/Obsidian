package io.github.vampirestudios.obsidian.api.obsidian.entity.components;

import com.google.gson.annotations.SerializedName;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import java.util.List;
import net.minecraft.resources.Identifier;

public class AgeableComponent extends Component {

    @SerializedName("drop_items")
    public List<Identifier> dropItems;
    public float duration;
    @SerializedName("feed_items")
    public List<Identifier> feedItems;
    @SerializedName("grow_up")
    public String growUp;

}