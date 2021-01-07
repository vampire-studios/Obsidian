package io.github.vampirestudios.obsidian.network;

import io.github.vampirestudios.obsidian.AnimationContext;
import io.github.vampirestudios.obsidian.EntityExt;
import io.github.vampirestudios.obsidian.Obsidian;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class PacketS2CAnimationControl implements AbstractPacket {

    private static final Identifier ID = Obsidian.id("anim_control");

    private AnimationCommand command;
    private Entity targetEntity;
    private Identifier animationId;
    private boolean affectAll;

    private int animAge;

    public Identifier getId() { return ID; }

    public PacketS2CAnimationControl() {}
    public PacketS2CAnimationControl(AnimationCommand command, Entity targetEntity, Identifier animationId, boolean affectAll) {
        this.command = command;
        this.targetEntity = targetEntity;
        this.animationId = animationId;
        this.affectAll = affectAll;
    }

    public PacketS2CAnimationControl(Entity targetEntity, Identifier animationId, int startAge) {
        this.command = AnimationCommand.LOAD_NBT;
        this.targetEntity = targetEntity;
        this.animationId = animationId;
        this.animAge = startAge;
    }

    @Override
    public void encode(PacketByteBuf buffer) {
        buffer.writeEnumConstant(command);
        buffer.writeVarInt(targetEntity.getEntityId());
        buffer.writeIdentifier(animationId);
        if(command == AnimationCommand.LOAD_NBT)
            buffer.writeVarInt(animAge);
        else
            buffer.writeBoolean(affectAll);
    }

    @Override
    public void decode(PacketByteBuf buffer) {
        this.command = buffer.readEnumConstant(AnimationCommand.class);
        this.targetEntity = MinecraftClient.getInstance().world.getEntityById(buffer.readVarInt());
        this.animationId = buffer.readIdentifier();
        if(command == AnimationCommand.LOAD_NBT)
            this.animAge = buffer.readVarInt();
        else
            this.affectAll = buffer.readBoolean();
    }

    @Override
    public void onReceive() {
        switch(this.command) {
            case ADD:
                EntityExt entity = (EntityExt)targetEntity;
                if(this.affectAll)
                    entity.getClientAnimations().clear();
                entity.getClientAnimations().put(new AnimationContext(this.animationId), targetEntity.age);
                break;
            case REMOVE:
                entity = (EntityExt)targetEntity;
                if(this.affectAll)
                    entity.getClientAnimations().clear();
                else
                    entity.getClientAnimations().keySet().stream().filter(ctx -> ctx.id.equals(animationId)).findFirst().ifPresent(entity.getClientAnimations()::remove);
                break;
            case LOAD_NBT:
                entity = (EntityExt)targetEntity;
                entity.getClientAnimations().put(new AnimationContext(this.animationId), targetEntity.age);
                break;
            case PAUSE_START:
            case CHANGE:
        }
    }

    public enum AnimationCommand {
        ADD,
        REMOVE,
        PAUSE_START,
        CHANGE,
        LOAD_NBT
    }
}