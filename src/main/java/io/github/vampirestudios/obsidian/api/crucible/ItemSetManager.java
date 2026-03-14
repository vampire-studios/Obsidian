package io.github.vampirestudios.obsidian.api.crucible;

import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class ItemSetManager {
    private static final ItemSetManager INSTANCE = new ItemSetManager();

    private ItemSetManager() {}

    public static ItemSetManager getInstance() {
        return INSTANCE;
    }

    /** Player UUID → set registry key string → active tier (-1 = none). */
    private final Map<UUID, Map<String, Integer>> activeTiers = new HashMap<>();

    public void onEquipmentChange(ServerPlayer player) {
        // Count equipped pieces per set
        Map<String, Integer> pieceCounts = new HashMap<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty()) continue;
            Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (itemId == null) continue;
            CrucibleItem crucibleItem = ContentRegistries.CRUCIBLE_ITEMS.getValue(itemId);
            if (crucibleItem == null || crucibleItem.Set == null) continue;
            pieceCounts.merge(crucibleItem.Set, 1, Integer::sum);
        }

        Map<String, Integer> playerTiers = activeTiers.computeIfAbsent(player.getUUID(), k -> new HashMap<>());

        for (CrucibleItemSet set : ContentRegistries.ITEM_SETS) {
            Identifier setId = ContentRegistries.ITEM_SETS.getKey(set);
            if (setId == null || set.Bonuses == null) continue;

            String setKey = setId.toString();
            // Match by full key or just path (allows "dragon_set" or "mymod:dragon_set")
            int pieces = pieceCounts.getOrDefault(setKey, pieceCounts.getOrDefault(setId.getPath(), 0));

            int newTier = findActiveTier(set, pieces);
            int oldTier = playerTiers.getOrDefault(setKey, -1);

            if (newTier == oldTier) continue;

            if (oldTier >= 0) {
                ItemSetBonus oldBonus = set.Bonuses.get(oldTier);
                if (oldBonus != null) removeBonuses(player, setKey, oldTier, oldBonus);
            }
            if (newTier >= 0) {
                ItemSetBonus newBonus = set.Bonuses.get(newTier);
                if (newBonus != null) applyBonuses(player, setKey, newTier, newBonus);
            }

            playerTiers.put(setKey, newTier);
        }
    }

    private int findActiveTier(CrucibleItemSet set, int pieces) {
        if (set.Bonuses == null) return -1;
        int best = -1;
        for (int tier : set.Bonuses.keySet()) {
            if (pieces >= tier && tier > best) best = tier;
        }
        return best;
    }

    private void applyBonuses(ServerPlayer player, String setKey, int tier, ItemSetBonus bonus) {
        if (bonus.Attributes != null) {
            bonus.Attributes.forEach((attrName, value) -> applyModifier(player, setKey, tier, attrName, value));
        }
        if (bonus.internalSkills != null) {
            SkillContext ctx = SkillContext.builder(player).level(player.serverLevel()).build();
            SkillManager.getInstance().executeSkills(bonus.internalSkills, SkillTrigger.ARMOR_EQUIP, ctx);
        }
    }

    private void removeBonuses(ServerPlayer player, String setKey, int tier, ItemSetBonus bonus) {
        if (bonus.Attributes == null) return;
        bonus.Attributes.keySet().forEach(attrName -> removeModifier(player, setKey, tier, attrName));
    }

    private void applyModifier(ServerPlayer player, String setKey, int tier, String attrName, double value) {
        var attrOpt = BuiltInRegistries.ATTRIBUTE.getHolder(
                net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ATTRIBUTE,
                        Identifier.tryParse(attrName)));
        attrOpt.ifPresent(attrHolder -> {
            AttributeInstance inst = player.getAttribute(attrHolder);
            if (inst == null) return;
            Identifier modId = modifierId(setKey, tier, attrName);
            inst.addOrReplacePermanentModifier(
                    new AttributeModifier(modId, value, AttributeModifier.Operation.ADD_VALUE));
        });
    }

    private void removeModifier(ServerPlayer player, String setKey, int tier, String attrName) {
        var attrOpt = BuiltInRegistries.ATTRIBUTE.getHolder(
                net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.ATTRIBUTE,
                        Identifier.tryParse(attrName)));
        attrOpt.ifPresent(attrHolder -> {
            AttributeInstance inst = player.getAttribute(attrHolder);
            if (inst != null) inst.removePermanentModifier(modifierId(setKey, tier, attrName));
        });
    }

    private static Identifier modifierId(String setKey, int tier, String attrName) {
        String safe = (setKey + "." + tier + "." + attrName).replace(':', '.').replace(' ', '_');
        // Truncate to fit ResourceLocation constraints
        if (safe.length() > 64) safe = safe.substring(0, 64);
        return Identifier.fromNamespaceAndPath("obsidian", "set." + safe);
    }

    public void clearPlayer(UUID playerId) {
        activeTiers.remove(playerId);
    }
}
