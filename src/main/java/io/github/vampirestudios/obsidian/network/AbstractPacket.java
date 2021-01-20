package io.github.vampirestudios.obsidian.network;

import net.fabricmc.fabric.api.network.PacketConsumer;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface AbstractPacket extends PacketConsumer {

    Identifier getId();

    void encode(PacketByteBuf buffer);
    void decode(PacketByteBuf buffer);

    void onReceive();

    default void accept(PacketContext context, PacketByteBuf buffer) {
        this.decode(buffer);
        context.getTaskQueue().execute(this::onReceive);
    }
}