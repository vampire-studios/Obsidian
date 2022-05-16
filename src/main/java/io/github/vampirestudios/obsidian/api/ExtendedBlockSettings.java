package io.github.vampirestudios.obsidian.api;

import net.minecraft.network.PacketByteBuf;

public interface ExtendedBlockSettings {
    void obsidian$write(PacketByteBuf buf);
}