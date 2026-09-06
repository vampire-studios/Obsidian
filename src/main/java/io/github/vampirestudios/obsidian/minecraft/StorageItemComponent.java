package io.github.vampirestudios.obsidian.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public record StorageItemComponent(int maxSlots, int maxWeightLimit, int weightInStorageItem,
                                   boolean allowNestedStorageItem, List<String> bannedItems) {
	public static final StorageItemComponent DEFAULT = new StorageItemComponent(64, 64, 4, true, List.of("minecraft:shulker_box"));

	public static final Codec<StorageItemComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.optionalFieldOf("max_slots", 64).forGetter(s -> s.maxSlots),
			Codec.INT.optionalFieldOf("max_weight_limit", 64).forGetter(s -> s.maxWeightLimit),
			Codec.INT.optionalFieldOf("weight_in_storage_item", 4).forGetter(s -> s.weightInStorageItem),
			Codec.BOOL.optionalFieldOf("allow_nested_storage_item", true).forGetter(s -> s.allowNestedStorageItem),
			Codec.STRING.listOf().optionalFieldOf("banned_items", List.of("minecraft:shulker_box")).forGetter(s -> s.bannedItems)
	).apply(instance, StorageItemComponent::new));

	public static final StreamCodec<ByteBuf, StorageItemComponent> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT,
			StorageItemComponent::maxSlots,
			ByteBufCodecs.VAR_INT,
			StorageItemComponent::maxWeightLimit,
			ByteBufCodecs.VAR_INT,
			StorageItemComponent::weightInStorageItem,
			ByteBufCodecs.BOOL,
			StorageItemComponent::allowNestedStorageItem,
			ByteBufCodecs.<ByteBuf, String>list().apply(ByteBufCodecs.STRING_UTF8),
			StorageItemComponent::bannedItems,
			StorageItemComponent::new
	);
}
