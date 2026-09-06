package io.github.vampirestudios.obsidian.api.bedrock;

import com.google.gson.annotations.SerializedName;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

public class Description {

	public static final MapCodec<Description> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
			Identifier.CODEC.fieldOf("identifier").forGetter(description -> description.identifier),
			Codec.BOOL.fieldOf("register_to_creative_menu").forGetter(description -> description.registerToCreativeMenu)
	).apply(instance, Description::new));

	public Identifier identifier;
	@SerializedName("register_to_creative_menu")
	public boolean registerToCreativeMenu;

	public Description(Identifier identifier, boolean registerToCreativeMenu) {
		this.identifier = identifier;
		this.registerToCreativeMenu = registerToCreativeMenu;
	}
}