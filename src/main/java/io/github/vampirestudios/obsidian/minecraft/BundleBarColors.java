package io.github.vampirestudios.obsidian.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ARGB;

public record BundleBarColors(int fullBarColor, int barColor) {

	public static final BundleBarColors DEFAULT = new BundleBarColors(
			ARGB.colorFromFloat(1.0F, 1.0F, 0.33F, 0.33F),
			ARGB.colorFromFloat(1.0F, 0.44F, 0.53F, 1.0F)
	);

	public static final Codec<BundleBarColors> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("full_bar_color").forGetter(BundleBarColors::fullBarColor),
			Codec.INT.fieldOf("bar_color").forGetter(BundleBarColors::barColor)
	).apply(instance, BundleBarColors::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, BundleBarColors> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT,
			BundleBarColors::fullBarColor,
			ByteBufCodecs.VAR_INT,
			BundleBarColors::barColor,
			BundleBarColors::new
	);
}
