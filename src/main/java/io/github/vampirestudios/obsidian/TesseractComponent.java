package io.github.vampirestudios.obsidian;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class TesseractComponent implements AutoSyncedComponent {

    private final Entity target;
    private Identifier animation;
    private long startTime;
    private int animLength;

    private boolean shouldLoop, freeze;

    @Environment(EnvType.CLIENT) private AnimationData data;

    public TesseractComponent(Entity e) {
        this.target = e;
        this.shouldLoop = true;
        this.freeze = false;
    }

    public static TesseractComponent get(Entity e) {
        return Obsidian.COMPONENT_ANIMATION.get(e);
    }

    public Entity getEntity() {
        return target;
    }

    public void startAnimation(Identifier id, int animLength, boolean shouldLoop) {
        this.animation = id;
        if(id != null) {
            this.animLength = animLength;
            this.startTime = getEntity().world.getTime();
            this.shouldLoop = shouldLoop;
        } else {
            this.startTime = this.animLength = -1;
            this.shouldLoop = true;
        }
    }

    @Override @Environment(EnvType.CLIENT)
    public void applySyncPacket(PacketByteBuf buf) {
        CompoundTag tag = buf.readCompoundTag();
        if (tag != null) {
            this.readFromNbt(tag);
            AnimationRegistry.INSTANCE.removeActiveData(data);
            Animation anim = AnimationRegistry.INSTANCE.getAnimation(this.animation);
            if(anim != null) {
                data = new AnimationData(anim, tag.getInt("animProgress"), shouldLoop, freeze);
                AnimationRegistry.INSTANCE.addActiveData(data);
            } else
                data = null;
        }
    }

    @Environment(EnvType.CLIENT)
    public AnimationData getData() {
        return data;
    }

    public Identifier getCurrentAnimation() {
        if(getEntity().world.getTime() - startTime >= animLength)
            animation = null;
        return animation;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        if(tag.getBoolean("hasAnimation")) {
            this.startTime = tag.getLong("startTime");
            this.animLength = tag.getInt("animLength");
            this.animation = new Identifier(tag.getString("animation"));
        } else {
            this.startTime = this.animLength = -1;
            this.animation = null;
        }
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putBoolean("hasAnimation", animation != null);
        if(animation != null) {
            tag.putLong("startTime", this.startTime);
            tag.putInt("animLength", this.animLength);
            tag.putInt("animProgress", (int)(getEntity().world.getTime() - this.startTime));
            tag.putString("animation", animation.toString());
        }
    }

}