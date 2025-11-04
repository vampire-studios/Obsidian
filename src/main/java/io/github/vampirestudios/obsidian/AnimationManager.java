/*
package io.github.vampirestudios.obsidian;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.AnimationDefinition.Builder;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AnimationManager extends SimpleJsonResourceReloadListener<JsonElement> implements IdentifiableResourceReloadListener {
    private static final AnimationDefinition FALLBACK_ANIMATION = AnimationDefinition.Builder.withLength(0.0f).build();
    private static final Gson GSON = new GsonBuilder().create();

    private Map<ResourceLocation, AnimationDefinition> animations;

    public AnimationManager() {
        super(GSON, "entity_animations");
        this.animations = Map.of();
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> data, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        ImmutableMap.Builder<ResourceLocation, AnimationDefinition> registry = new ImmutableMap.Builder<>();
        data.forEach((resourceLocation, jsonElement) -> {
            try {
                AnimationHolder animationHolder = AnimationHolder.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonParseException::new);
                registry.put(resourceLocation, this.createAnimation(animationHolder));
            }
            catch (Exception exception) {
                Obsidian.LOGGER.error("Failed to parse custom animation {}: {}", resourceLocation, exception);
            }
        });
        this.animations = registry.buildOrThrow();
    }

    public AnimationDefinition getAnimation(ResourceLocation id) {
        if (this.animations.containsKey(id)) {
            return this.animations.get(id);
        }
        return FALLBACK_ANIMATION;
    }

    private AnimationDefinition createAnimation(AnimationHolder data) {
        Builder builder = Builder.withLength(data.length());
        var bones = data.bones();
        bones.forEach((bone, _) -> {
            var targets = bones.get(bone);
            targets.forEach((target, timestampMap) -> {
                var timestamps = targets.get(target);
                Keyframe[] keyframes = new Keyframe[timestampMap.size()];
                AtomicInteger current = new AtomicInteger(0);

                timestamps.forEach((timestamp, keyframe) -> {
                    keyframes[current.get()] = new Keyframe(Float.parseFloat(timestamp), target.transformTarget(keyframe.transformation()), keyframe.interpolation().getInterpolationFromType());
                    current.addAndGet(1);
                });
                builder.addAnimation(bone, new AnimationChannel(target.getTarget(), keyframes));
            });
        });
        if (data.looping()) builder.looping();
        return builder.build();
    }

    public ResourceLocation getFabricId() {
        return Obsidian.id("animation_manager");
    }
}*/
