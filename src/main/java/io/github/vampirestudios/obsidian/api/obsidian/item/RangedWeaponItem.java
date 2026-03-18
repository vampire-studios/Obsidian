package io.github.vampirestudios.obsidian.api.obsidian.item;

public class RangedWeaponItem extends Item {

    public enum Type {
        @com.google.gson.annotations.SerializedName("bow")      BOW,
        @com.google.gson.annotations.SerializedName("crossbow") CROSSBOW,
        @com.google.gson.annotations.SerializedName("trident")  TRIDENT
    }
    public Type weapon_type;

}