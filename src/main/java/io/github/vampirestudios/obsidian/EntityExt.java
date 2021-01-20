package io.github.vampirestudios.obsidian;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

public interface EntityExt {

    void addAnimation(Identifier id, boolean set);
    void addAnimationNbt(Identifier id, int startAge);
    void updateTime(Identifier id, int newAge);
    void removeAnimation(Identifier id, boolean all);

    Object2IntMap<Identifier> getServerAnimationData();
    @Environment(EnvType.CLIENT)
    Object2IntMap<AnimationContext> getClientAnimations();
}