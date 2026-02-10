package io.github.vampirestudios.obsidian.api.obsidian.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.EnumMap;
import java.util.Map;

public class ArmorMaterial {

    public Identifier name;
    public int enchantability;
    @JsonProperty("repair_item")
    @SerializedName("repair_item")
    public Identifier repairItem;
    @JsonProperty("equip_sound")
    @SerializedName("equip_sound")
    public Identifier equipSound;
    public float toughness;
    public float knockback_resistance;
    public Identifier repair_tag;
    public int durability;
    public Map<ArmorType, Integer> defense = net.minecraft.util.Util.make(new EnumMap<>(ArmorType.class), (map) -> {
        map.put(ArmorType.BOOTS, 1);
        map.put(ArmorType.LEGGINGS, 2);
        map.put(ArmorType.CHESTPLATE, 3);
        map.put(ArmorType.HELMET, 1);
        map.put(ArmorType.BODY, 3);
    });

    public Holder<SoundEvent> getEquipSound() {
        var soundEvent = BuiltInRegistries.SOUND_EVENT.get(equipSound);
        return soundEvent.orElse(BuiltInRegistries.SOUND_EVENT.createIntrusiveHolder(SoundEvents.ARMOR_EQUIP_LEATHER.value()));
    }
}