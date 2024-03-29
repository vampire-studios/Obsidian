package io.github.vampirestudios.obsidian.api.bedrock.item;

import com.google.gson.annotations.SerializedName;
import net.minecraft.resources.ResourceLocation;

public class Component {

    @SerializedName("minecraft:fuel")
    public Fuel fuel;

    @SerializedName("minecraft:knockback_resistance")
    public KnockbackResistance knockbackResistance;

    @SerializedName("minecraft:icon")
    public Icon icon;

    @SerializedName("minecraft:projectile")
    public Projectile projectile;

    @SerializedName("minecraft:durability")
    public Durability durability;

    public static class Fuel {
        public float duration;
    }

    public static class OnUse {
        public ResourceLocation event;
    }

    public static class OnUseOn {
        public ResourceLocation event;
    }

    public static class Projectile {
        public int minimum_critical_power;
        public ResourceLocation projectile_entity;
    }

    public static class Durability {
        public int damage_chance;
        public int max_durability;
    }

    public static class DisplayName {
        public String value;
    }

    public static class Food {
        public boolean can_always_eat;
        public int nutrition;
        public int saturation_modifier;
        public ResourceLocation using_converts_to;
        public ResourceLocation on_consume;
    }

    public static class KnockbackResistance {
        public float protection;
    }

    public static class Icon {
        public int frame;
        public ResourceLocation texture;
    }

    public static class EntityCollision {

        public float[] origin = new float[]{-8.0F, 0.0F, -8.0F};
        public float[] size = new float[]{16.0F, 16.0F, 16.0F};

    }
}