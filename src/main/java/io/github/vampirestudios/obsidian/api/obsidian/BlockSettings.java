package io.github.vampirestudios.obsidian.api.obsidian;

import blue.endless.jankson.annotation.SerializedName;
import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.api.MapColors;
import io.github.vampirestudios.obsidian.api.VanillaSoundEvents;
import io.github.vampirestudios.obsidian.api.obsidian.block.CustomSoundGroup;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import java.util.Locale;
import java.util.Map;

public class BlockSettings {
    @SerializedName("parent")
    @com.google.gson.annotations.SerializedName("parent")
    public Object baseBlockSettings;

    @SerializedName("sound_group")
    @com.google.gson.annotations.SerializedName("sound_group")
    public Object soundGroup = Identifier.withDefaultNamespace("stone");

    public boolean collidable = true;
    public float hardness = 3.0F;
    public float resistance = 3.0F;
    public boolean randomTicks = false;
    public boolean instant_break = false;
    public float slipperiness = 0.6F;
    public Identifier drop = Identifier.withDefaultNamespace("stone");
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
			case Identifier resourceLocation -> {
				if (!resourceLocation.getNamespace().equals("minecraft")) {
					CustomSoundGroup customSoundGroup = ContentRegistries.BLOCK_SOUND_GROUPS.getValue(resourceLocation);
					assert customSoundGroup != null;
					return createSoundType(customSoundGroup);
				} else {
					return VanillaSoundEvents.get(resourceLocation);
				}
			}
			case String s -> {
				Identifier location = Identifier.tryParse(s);
				assert location != null;
				if (!location.getNamespace().equals("minecraft")) {
					CustomSoundGroup customSoundGroup = ContentRegistries.BLOCK_SOUND_GROUPS.getValue(location);
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
        SoundEvent breakSound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.getValue(customSoundGroup.break_sound);
        SoundEvent stepSound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.getValue(customSoundGroup.step_sound);
        SoundEvent placeSound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.getValue(customSoundGroup.place_sound);
        SoundEvent hitSound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.getValue(customSoundGroup.hit_sound);
        SoundEvent fallSound = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.getValue(customSoundGroup.fall_sound);
        return new SoundType(1.0F, 1.0F, breakSound, stepSound, placeSound, hitSound, fallSound);
    }

    // This getter will handle the different possible types of 'itemSettings'
    public BlockSettings getParentSettings() {
        switch (baseBlockSettings) {
            case Map<?, ?> propertiesMap -> {
                System.out.println(STR."Map: \{propertiesMap}");
                return constructBlockSettingsFromMap(propertiesMap);
            }
            case JsonObject jsonObject -> {
                System.out.println(STR."Json Object: \{jsonObject.getAsString()}");
                return null;
            }
            case String s -> {
                System.out.println(STR."String: \{s}");
                return getBlockSettingsFromReference(s);
            }
            case BlockSettings blockSettings -> {
                return blockSettings;
            }
            case null, default -> {
                return handleUnknownBlockSettingsType();
            }
        }
    }

    private BlockSettings constructBlockSettingsFromMap(Map<?, ?> propertiesMap) {
//		System.out.println("Map: " + propertiesMap);
        BlockSettings settings = new BlockSettings();
        if (propertiesMap.containsKey("parent")) {
            settings.baseBlockSettings = ContentRegistries.BLOCK_SETTINGS.get(Identifier.tryParse((String) propertiesMap.get("parent")));
        }
        return settings; // Replace with actual construction logic
    }

    private BlockSettings getBlockSettingsFromReference(String reference) {
        Identifier location = Identifier.tryParse(reference);
        if (location != null) {
            return ContentRegistries.BLOCK_SETTINGS.getValue(location);
        } else {
            System.out.println(STR."Invalid Reference: \{reference}");
            return handleInvalidReference(reference);
        }
    }

    private BlockSettings handleUnknownBlockSettingsType() {
//		System.out.println("Unknown Item Settings Type");
        return new BlockSettings(); // Replace with actual error handling logic
    }

    private BlockSettings handleInvalidReference(String reference) {
//		System.out.println("Invalid reference: " + reference);
        return new BlockSettings(); // Replace with actual error handling logic
    }
}