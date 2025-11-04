package io.github.vampirestudios.obsidian.api.crucible;

import io.github.vampirestudios.obsidian.api.crucible.SkillEntry.Buff;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuffManager {
    private Map<LivingEntity, List<Buff>> activeBuffs = new HashMap<>();

    public void applyBuff(LivingEntity entity, Buff buff) {
        activeBuffs.computeIfAbsent(entity, k -> new ArrayList<>()).add(buff);
    }

    public void removeExpiredBuffs() {
        for (List<Buff> buffs : activeBuffs.values()) {
            buffs.removeIf(Buff::isExpired);
        }
    }

    public int getActiveStacks(LivingEntity entity, String buffType) {
        return activeBuffs.getOrDefault(entity, new ArrayList<>()).stream()
                          .filter(buff -> buff.getType().equals(buffType))
                          .mapToInt(Buff::getCurrentStacks)
                          .sum();
    }
}
