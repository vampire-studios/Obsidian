package io.github.vampirestudios.obsidian.api.obsidian.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.JsonObject;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.EnumMap;
import java.util.Map;

public class ArmorMaterial {

    public ResourceLocation name;
    public int enchantability;
    @JsonProperty("equip_sound") public ResourceLocation equipSound;
    @JsonProperty("repair_item") public ResourceLocation repairItem;
    public float toughness;
    public float knockback_resistance;
    public ResourceLocation repair_tag;
    public Object durability;
    public Map<ArmorType, Integer> defense = Util.make(new EnumMap<>(ArmorType.class), (map) -> {
        map.put(ArmorType.BOOTS, 1);
        map.put(ArmorType.LEGGINGS, 2);
        map.put(ArmorType.CHESTPLATE, 3);
        map.put(ArmorType.HELMET, 1);
        map.put(ArmorType.BODY, 3);
    });

    public int getDurability(EquipmentSlot slot) {
        if (durability instanceof Integer integer) {
            return integer;
        } else if (durability instanceof JsonObject) {
            Durability durability1 = (Durability) durability;
            return switch(slot) {
				case FEET -> durability1.bootsDurability;
                case LEGS -> durability1.legginsDurability;
                case CHEST -> durability1.chetsplateDurability;
                case HEAD -> durability1.helmetDurability;
				default -> 0;
			};
        } else {
            return 0;
        }
    }

    public ResourceLocation texturePath;
    public ResourceLocation texture1;
    public ResourceLocation texture2;
    public ResourceLocation customArmorModel;

    public static class Durability {
        public int helmetDurability;
        public int chetsplateDurability;
        public int legginsDurability;
        public int bootsDurability;
    }
}