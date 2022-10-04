package io.github.vampirestudios.obsidian.api.bedrock;

import blue.endless.jankson.annotation.SerializedName;
import net.minecraft.util.Identifier;

public class Description {

    public Identifier identifier;
    @SerializedName("register_to_creative_menu") public boolean registerToCreativeMenu;

}