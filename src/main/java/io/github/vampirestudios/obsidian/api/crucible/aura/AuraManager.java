package io.github.vampirestudios.obsidian.api.crucible.aura;

import net.minecraft.world.entity.LivingEntity;

import java.util.*;

public class AuraManager {
    private static final List<Aura> activeAuras = new ArrayList<>();
    private static final Map<LivingEntity, List<Aura>> entityAuras = new HashMap<>(); // Tracks auras by entity

    public static void addAura(Aura aura) {
        activeAuras.add(aura);
        entityAuras.computeIfAbsent(aura.getCaster(), k -> new ArrayList<>()).add(aura);
    }

    public static void tick() {
        Iterator<Aura> iterator = activeAuras.iterator();
        while (iterator.hasNext()) {
            Aura aura = iterator.next();
            aura.tick();
            if (aura.isExpired()) {
                iterator.remove();
                entityAuras.get(aura.getCaster()).remove(aura);
            }
        }
    }

    public static boolean hasAura(LivingEntity entity, String auraId) {
        List<Aura> auras = entityAuras.get(entity);
        if (auras != null) {
            for (Aura aura : auras) {
                if (aura.getId().equals(auraId)) {
                    return true;
                }
            }
        }
        return false;
    }
}
