package io.github.vampirestudios.obsidian.api.obsidian;

import blue.endless.jankson.annotation.SerializedName;
import io.github.vampirestudios.obsidian.api.MapColors;
import io.github.vampirestudios.obsidian.api.VanillaSoundEvents;
import io.github.vampirestudios.obsidian.api.obsidian.block.CustomSoundGroup;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.Locale;

public class BlockSettings {
    @SerializedName("sound_group")
    @com.google.gson.annotations.SerializedName("sound_group")
    public Object soundGroup = new ResourceLocation("stone");

    public boolean collidable = true;
    public float hardness = 3.0F;
    public float resistance = 3.0F;
    public boolean randomTicks = false;
    public boolean instant_break = false;
    public float slipperiness = 0.6F;
    public ResourceLocation drop = new ResourceLocation("stone");
    public float velocity_modifier = 1.0F;
    public float jump_velocity_modifier = 1.0F;
    public int luminance = 0;
    public boolean is_emissive = false;
    public boolean translucent = true;
    public boolean dynamic_boundaries = false;
    public String push_reaction = "NORMAL";
    public String map_color = "STONE";

    public MapColor getMapColor() {
        return MapColors.get(map_color);
    }

    public PushReaction getPushReaction() {
        return switch (push_reaction.toUpperCase(Locale.ROOT)) {
            case "NORMAL" -> PushReaction.NORMAL;
            case "DESTROY" -> PushReaction.DESTROY;
            case "BLOCK" -> PushReaction.BLOCK;
            case "IGNORE" -> PushReaction.IGNORE;
            case "PUSH_ONLY" -> PushReaction.PUSH_ONLY;
            default -> throw new IllegalStateException("Unexpected value: " + push_reaction);
        };
    }

    public SoundType getBlockSoundGroup() {
        switch (soundGroup) {
            case ResourceLocation resourceLocation -> {
                if (!resourceLocation.getNamespace().equals("minecraft")) {
                    CustomSoundGroup customSoundGroup = ContentRegistries.BLOCK_SOUND_GROUPS.get(resourceLocation);
                    assert customSoundGroup != null;
                    return createSoundType(customSoundGroup);
                } else {
                    return VanillaSoundEvents.get(resourceLocation);
                }
            }
            case String resourceLocation -> {
                ResourceLocation location = ResourceLocation.tryParse(resourceLocation);
                assert location != null;
                if (!location.getNamespace().equals("minecraft")) {
                    CustomSoundGroup customSoundGroup = ContentRegistries.BLOCK_SOUND_GROUPS.get(location);
                    assert customSoundGroup != null;
                    return createSoundType(customSoundGroup);
                } else {
                    return VanillaSoundEvents.get(location);
                }
            }
            case CustomSoundGroup customSoundGroup -> {
                return createSoundType(customSoundGroup);
            }
            case null, default -> {
                System.out.println(soundGroup.toString());
                return SoundType.STONE;
            }
        }
    }

    private SoundType createSoundType(CustomSoundGroup customSoundGroup) {
        SoundEvent breakSound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(customSoundGroup.break_sound);
        SoundEvent stepSound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(customSoundGroup.step_sound);
        SoundEvent placeSound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(customSoundGroup.place_sound);
        SoundEvent hitSound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(customSoundGroup.hit_sound);
        SoundEvent fallSound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(customSoundGroup.fall_sound);
        return new SoundType(1.0F, 1.0F, breakSound, stepSound, placeSound, hitSound, fallSound);
    }
}