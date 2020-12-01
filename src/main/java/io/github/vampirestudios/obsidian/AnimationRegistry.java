package io.github.vampirestudios.obsidian;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Environment(EnvType.CLIENT)
public class AnimationRegistry implements SimpleSynchronousResourceReloadListener {

    public static final AnimationRegistry INSTANCE = new AnimationRegistry();
    private final Identifier ID = Obsidian.id("anim_reload_listener");

    private final Map<Identifier, Animation> animations = new HashMap<>();
    private final List<AnimationData> activeData = new LinkedList<>();

    private AnimationRegistry() {
        ClientTickCallback.EVENT.register(c -> {
            if(c.world != null && !c.isPaused()) {
                synchronized (activeData) {
                    activeData.removeIf(data -> !data.tick());
                }
            } else {
                activeData.clear();
            }
        });
    }

    public void addAnimationMap(Map<Identifier, Animation> map) {
        animations.putAll(map);
    }

    public Animation getAnimation(Identifier id) {
        return animations.get(id);
    }

    public void addActiveData(AnimationData d) {
        synchronized (activeData) {
            activeData.add(d);
        }
    }

    public void removeActiveData(AnimationData d) {
        synchronized (activeData) {
            activeData.remove(d);
        }
    }

    @Override
    public void apply(ResourceManager manager) {
        animations.values().forEach(Animation::load);
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }
}