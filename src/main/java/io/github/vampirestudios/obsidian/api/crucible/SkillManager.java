package io.github.vampirestudios.obsidian.api.crucible;

import net.minecraft.resources.Identifier;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SkillManager {

    private final Map<Identifier, Skill> skillRegistry = new ConcurrentHashMap<>();
    private final Map<SkillTrigger, CopyOnWriteArrayList<Skill>> skillTriggerMap = new ConcurrentHashMap<>();
    private final Map<SkillTrigger, Map<SkillScope, List<Skill>>> byTrigger = new ConcurrentHashMap<>();

    private static final SkillManager INSTANCE = new SkillManager();
    private SkillManager() {}

    public static SkillManager getInstance() {
        return INSTANCE;
    }

    public void registerSkill(Identifier skillId, Skill skill) {
        Objects.requireNonNull(skillId, "skillId");
        Objects.requireNonNull(skill, "skill");

        Skill prev = skillRegistry.putIfAbsent(skillId, skill);
        if (prev != null) {
            throw new IllegalArgumentException(STR."Skill ID already registered: \{skillId}");
        }

        // ALSO register by trigger so triggerSkills() works
        registerSkill(skill);
    }

    public Skill getSkillById(Identifier skillId) {
        return skillRegistry.get(skillId);
    }

    public void registerSkill(Skill skill) {
        Objects.requireNonNull(skill, "skill");
        Objects.requireNonNull(skill.trigger, "skill.trigger");
        SkillScope scope = skill.scope == null ? SkillScope.GLOBAL : skill.scope;

        byTrigger.computeIfAbsent(skill.trigger, _ -> new ConcurrentHashMap<>())
                .computeIfAbsent(scope, _ -> Collections.synchronizedList(new ArrayList<>()))
                .add(skill);
    }

    public void triggerSkills(SkillTrigger trigger, SkillContext ctx, SkillScope scope) {
        Map<SkillScope, List<Skill>> scopes = byTrigger.get(trigger);
        if (scopes == null) return;

        List<Skill> skills = scopes.get(scope);
        if (skills == null || skills.isEmpty()) return;

        for (Skill skill : skills) {
            // Your existing repeat scheduling logic can stay here
            if (skill.repeat <= 1) {
                executeSkill(skill, ctx);
            } else {
                for (int i = 0; i < skill.repeat; i++) {
                    int delay = i * skill.repeatInterval;
                    SkillScheduler.scheduleSkillExecution(delay, () -> executeSkill(skill, ctx));
                }
            }
        }
    }

    private void executeSkill(Skill skill, SkillContext ctx) {
        // conditions
        if (ctx.hasTarget()) {
            if (!skill.evaluateConditions(ctx.caster, ctx.target)) return;
            skill.applyEffect(ctx.caster, ctx.target);
            return;
        }
        if (ctx.hasPosition()) {
            // if you want pos-conditions, add later
            skill.applyEffect(ctx.caster, ctx.position);
            return;
        }
        skill.applyEffect(ctx.caster);
    }
}
