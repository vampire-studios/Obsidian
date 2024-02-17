package io.github.vampirestudios.obsidian.api.bedrock;

import blue.endless.jankson.annotation.SerializedName;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public class Description {

    public static final MapCodec<Description> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("identifier").forGetter(description -> description.identifier),
            Codec.BOOL.fieldOf("register_to_creative_menu").forGetter(description -> description.registerToCreativeMenu)
    ).apply(instance, Description::new));

    public ResourceLocation identifier;
    @SerializedName("register_to_creative_menu") public boolean registerToCreativeMenu;

    public Description(ResourceLocation identifier, boolean registerToCreativeMenu) {
        this.identifier = identifier;
        this.registerToCreativeMenu = registerToCreativeMenu;
    }
}