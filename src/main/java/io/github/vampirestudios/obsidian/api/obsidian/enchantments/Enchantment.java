package io.github.vampirestudios.obsidian.api.obsidian.enchantments;

import com.google.gson.annotations.SerializedName;
import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;

public class Enchantment {

    public NameInformation name;
    public String[] slots;
    @SerializedName("accepted_items") public Identifier[] acceptedItems;
    @SerializedName("blacklisted_enchantments") public Identifier[] blacklistedEnchantments;
    public String target = "breakable";
    public String rarity = "common";
    @SerializedName("minimum_level") public int minimumLevel = 1;
    @SerializedName("maximum_level") public int maximumLevel = 1;
    @SerializedName("base_cost") public int baseCost = 1;
    @SerializedName("per_level_cost") public int perLevelCost = 10;
    @SerializedName("random_cost") public int randomCost = 5;
    @SerializedName("minimum_power") public int minimumPower;
    @SerializedName("maximum_power") public int maximumPower;
    @SerializedName("protection_amounts") public ProtectionAmount[] protectionAmounts;
    @SerializedName("attack_damages") public AttackDamage[] attackDamages;
    public boolean treasure = false;
    public boolean curse = false;
    public boolean tradeable = true;
    public boolean discoverable = true;
    @SerializedName("allow_on_books") public boolean allowOnBooks = true;

    public net.minecraft.enchantment.Enchantment.Rarity getRarity() {
        return net.minecraft.enchantment.Enchantment.Rarity.valueOf(rarity);
    }

    public int getMinimumPower(int level) {
        if (minimumPower != 0) {
            return minimumPower;
        } else {
            return baseCost + level * perLevelCost;
        }
    }

    public int getMaximumPower(int level) {
        if (maximumPower != 0) {
            return maximumPower;
        } else {
            return getMinimumPower(level) + randomCost;
        }
    }

    public EnchantmentTarget getEnchantmentTarget() {
        return EnchantmentTarget.valueOf(target);
    }

    public EquipmentSlot[] getEquipmentSlots() {
        EquipmentSlot[] equipmentSlots = new EquipmentSlot[10];
        for (int i = 0; i < this.slots.length; i++) {
            equipmentSlots[i] = EquipmentSlot.byName(this.slots[i]);
        }
        return equipmentSlots;
    }

}
