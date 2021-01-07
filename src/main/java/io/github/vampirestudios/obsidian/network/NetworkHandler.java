package io.github.vampirestudios.obsidian.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;

public final class NetworkHandler {

    public NetworkHandler() {
        register(new PacketC2SRemoveAnimation());
    }

    public void sendToClient(PlayerEntity entity, AbstractPacket packet) {
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        packet.encode(buffer);
        ServerSidePacketRegistry.INSTANCE.sendToPlayer(entity, packet.getId(), buffer);
    }

    private void register(AbstractPacket packet) {
        ClientSidePacketRegistry.INSTANCE.register(packet.getId(), packet);
    }

}