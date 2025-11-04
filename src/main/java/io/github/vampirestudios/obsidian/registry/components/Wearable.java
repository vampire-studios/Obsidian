package io.github.vampirestudios.obsidian.registry.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;

public record Wearable(boolean dispensable, EquipmentSlotGroup slot, boolean hasOverlay, ResourceLocation overlayTexture) {
	public static final Codec<Wearable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.fieldOf("dispensable").forGetter(Wearable::dispensable),
			EquipmentSlotGroup.CODEC.fieldOf("slot").forGetter(Wearable::slot),
			Codec.BOOL.fieldOf("has_overlay").forGetter(Wearable::hasOverlay),
			ResourceLocation.CODEC.fieldOf("overlay_texture").forGetter(Wearable::overlayTexture)
	).apply(instance, Wearable::new));

	public static final StreamCodec<RegistryFriendlyByteBuf, Wearable> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL,
			Wearable::dispensable,
			EquipmentSlotGroup.STREAM_CODEC,
			Wearable::slot,
			ByteBufCodecs.BOOL,
			Wearable::hasOverlay,
			ResourceLocation.STREAM_CODEC,
			Wearable::overlayTexture,
			Wearable::new
	);
}
