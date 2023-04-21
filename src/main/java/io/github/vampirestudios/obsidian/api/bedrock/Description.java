package io.github.vampirestudios.obsidian.api.bedrock;

import blue.endless.jankson.annotation.SerializedName;
import net.minecraft.resources.ResourceLocation;

public class Description {

    public ResourceLocation identifier;
    @SerializedName("register_to_creative_menu") public boolean registerToCreativeMenu;

}