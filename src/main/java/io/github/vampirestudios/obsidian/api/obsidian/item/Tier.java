package io.github.vampirestudios.obsidian.api.obsidian.item;

import blue.endless.jankson.annotation.SerializedName;
import net.minecraft.resources.Identifier;

import java.util.List;

public class Tier {

    public int durability;
    @SerializedName("mining_speed")
    @com.google.gson.annotations.SerializedName("mining_speed")
    public float miningSpeed;
    @SerializedName("attack_damage")
    @com.google.gson.annotations.SerializedName("attack_damage")
    public float attackDamage;
    public int enchantability;
    @SerializedName("repair_item")
    @com.google.gson.annotations.SerializedName("repair_item")
    public List<Identifier> repairItem;
    @SerializedName("incorrect_blocks_for_drops")
    @com.google.gson.annotations.SerializedName("incorrect_blocks_for_drops")
    public Identifier incorrectBlocksForDrops;
}