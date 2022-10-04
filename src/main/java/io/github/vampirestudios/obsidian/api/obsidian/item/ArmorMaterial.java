package io.github.vampirestudios.obsidian.api.obsidian.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonObject;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;

public class ArmorMaterial {

    public Identifier name;
    public int protection_amount;
    public int enchantability;
    @JsonProperty("equip_sound") public Identifier equipSound;
    public float toughness;
    public float knockback_resistance;
    public Identifier repair_item;
    public Object durability;

    public int getDurability(EquipmentSlot slot) {
        if (durability instanceof Integer integer) {
            return integer;
        } else if (durability instanceof JsonObject) {
            Durability durability1 = (Durability) durability;
            return switch(slot) {
                default -> 0;
                case FEET -> durability1.bootsDurability;
                case LEGS -> durability1.legginsDurability;
                case CHEST -> durability1.chetsplateDurability;
                case HEAD -> durability1.helmetDurability;
            };
        } else {
            return 0;
        }
    }

    public Identifier texture1;
    public Identifier texture2;
    public Identifier customArmorModel;

    public static class Durability {
        public int helmetDurability;
        public int chetsplateDurability;
        public int legginsDurability;
        public int bootsDurability;
    }
}