package io.github.vampirestudios.obsidian.network;

import io.github.vampirestudios.obsidian.EntityExt;
import io.github.vampirestudios.obsidian.Obsidian;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class PacketC2SLoopAnimation implements AbstractPacket {

    private static final Identifier ID = Obsidian.id("remove_anim_client");

    private Entity targetEntity;
    private Identifier id;
    private int newAge;

    public PacketC2SLoopAnimation() { }
    public PacketC2SLoopAnimation(Entity targetEntity, Identifier id, int newAge) {
        this.targetEntity = targetEntity;
        this.id = id;
        this.newAge = newAge;
    }

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    public void encode(PacketByteBuf buffer) {
        buffer.writeVarInt(targetEntity.getId());
        buffer.writeIdentifier(id);
        buffer.writeVarInt(newAge);
    }

    @Override
    public void decode(PacketByteBuf buffer) {
        this.targetEntity = MinecraftClient.getInstance().world.getEntityById(buffer.readVarInt());
        this.id = buffer.readIdentifier();
        this.newAge = buffer.readVarInt();
    }

    @Override
    public void onReceive() {
        ((EntityExt)targetEntity).updateTime(id, newAge);
    }
}