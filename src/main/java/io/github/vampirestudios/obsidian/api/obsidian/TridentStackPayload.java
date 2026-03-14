package io.github.vampirestudios.obsidian.api.obsidian;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import org.quiltmc.qsl.item.extension.impl.trident.TridentClientModInitializer;

public record TridentStackPayload(ItemStack stack) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<TridentStackPayload> TYPE =
			new CustomPacketPayload.Type<>(TridentClientModInitializer.TRIDENT_SPAWN_PACKET_ID);
	public static final StreamCodec<RegistryFriendlyByteBuf, TridentStackPayload> CODEC =
			StreamCodec.composite(ItemStack.STREAM_CODEC, TridentStackPayload::stack, TridentStackPayload::new);

	@Override
	public CustomPacketPayload.Type<TridentStackPayload> type() {
		return TYPE;
	}
}