package io.github.vampirestudios.obsidian.api.crucible;

import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.registry.OItemComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class AugmentManager {
    private static final AugmentManager INSTANCE = new AugmentManager();

    private AugmentManager() {}

    public static AugmentManager getInstance() {
        return INSTANCE;
    }

    /** Player UUID → list of (attribute id, modifier id) pairs currently applied from augments. */
    private final Map<UUID, List<ActiveModifier>> activeModifiers = new HashMap<>();

    private record ActiveModifier(Identifier attributeId, Identifier modifierId) {}

    public void onEquipmentChange(ServerPlayer player) {
        removeAllBonuses(player);

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            applyFromStack(player, player.getItemBySlot(slot));
        }
    }

    private void applyFromStack(ServerPlayer player, ItemStack stack) {
        if (stack.isEmpty()) return;
        AugmentSocketData data = stack.get(OItemComponents.AUGMENT_SOCKETS);
        if (data == null || data.slots().isEmpty()) return;

        for (int i = 0; i < data.slots().size(); i++) {
            AugmentSlotEntry slot = data.slots().get(i);
            if (slot.isEmpty()) continue;

            Identifier augId = Identifier.tryParse(slot.augmentId().get());
            CrucibleAugment augment = augId != null ? ContentRegistries.AUGMENTS.getValue(augId) : null;
            if (augment == null || augment.Attributes == null) continue;

            int slotIndex = i;
            augment.Attributes.forEach((attrName, value) -> {
                Identifier attrId = Identifier.tryParse(attrName);
                if (attrId == null) return;
                var attrOpt = BuiltInRegistries.ATTRIBUTE.getHolder(
                        net.minecraft.resources.ResourceKey.create(
                                net.minecraft.core.registries.Registries.ATTRIBUTE, attrId));
                attrOpt.ifPresent(attrHolder -> {
                    AttributeInstance inst = player.getAttribute(attrHolder);
                    if (inst == null) return;
                    Identifier modId = augModifierId(slot.augmentId().get(), slotIndex, attrName);
                    inst.addOrReplacePermanentModifier(
                            new AttributeModifier(modId, value, AttributeModifier.Operation.ADD_VALUE));
                    activeModifiers.computeIfAbsent(player.getUUID(), k -> new ArrayList<>())
                            .add(new ActiveModifier(attrId, modId));
                });
            });
        }
    }

    private void removeAllBonuses(ServerPlayer player) {
        List<ActiveModifier> mods = activeModifiers.remove(player.getUUID());
        if (mods == null) return;
        for (ActiveModifier mod : mods) {
            var attrOpt = BuiltInRegistries.ATTRIBUTE.getHolder(
                    net.minecraft.resources.ResourceKey.create(
                            net.minecraft.core.registries.Registries.ATTRIBUTE, mod.attributeId()));
            attrOpt.ifPresent(attrHolder -> {
                AttributeInstance inst = player.getAttribute(attrHolder);
                if (inst != null) inst.removePermanentModifier(mod.modifierId());
            });
        }
    }

    /** Returns the internalSkills list of all socketed augments across all equipment slots. */
    public List<Skill> getAugmentSkills(ServerPlayer player, EquipmentSlot slot) {
        ItemStack stack = player.getItemBySlot(slot);
        if (stack.isEmpty()) return List.of();
        AugmentSocketData data = stack.get(OItemComponents.AUGMENT_SOCKETS);
        if (data == null) return List.of();

        List<Skill> skills = new ArrayList<>();
        for (AugmentSlotEntry slotEntry : data.slots()) {
            if (slotEntry.isEmpty()) continue;
            Identifier augId = Identifier.tryParse(slotEntry.augmentId().get());
            CrucibleAugment augment = augId != null ? ContentRegistries.AUGMENTS.getValue(augId) : null;
            if (augment != null && augment.internalSkills != null) {
                skills.addAll(augment.internalSkills);
            }
        }
        return skills;
    }

    public void clearPlayer(UUID playerId) {
        activeModifiers.remove(playerId);
    }

    private static Identifier augModifierId(String augmentId, int slotIndex, String attrName) {
        String safe = (augmentId + "." + slotIndex + "." + attrName).replace(':', '.').replace(' ', '_');
        if (safe.length() > 64) safe = safe.substring(0, 64);
        return Identifier.fromNamespaceAndPath("obsidian", "aug." + safe);
    }
}
