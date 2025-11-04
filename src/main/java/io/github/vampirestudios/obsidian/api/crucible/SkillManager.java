package io.github.vampirestudios.obsidian.api.crucible;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SkillManager {
    // A map to store skills by their unique ID
    private final Map<ResourceLocation, Skill> skillRegistry = new ConcurrentHashMap<>();

    // Singleton instance for easy access
    private static final SkillManager INSTANCE = new SkillManager();

    // Private constructor for singleton pattern
    private SkillManager() {}

    // Get the singleton instance of the SkillManager
    public static SkillManager getInstance() {
        return INSTANCE;
    }

    /**
     * Registers a skill with a unique ID.
     *
     * @param skillId The unique identifier for the skill.
     * @param skill The skill instance to be registered.
     */
    public void registerSkill(ResourceLocation skillId, Skill skill) {
        if (skillRegistry.containsKey(skillId)) {
            throw new IllegalArgumentException(STR."Skill ID already registered: \{skillId}");
        }
        skillRegistry.put(skillId, skill);
    }

    /**
     * Retrieves a skill by its ID.
     *
     * @param skillId The unique identifier for the skill.
     * @return The skill instance if found, or null if no skill is registered with that ID.
     */
    public Skill getSkillById(ResourceLocation skillId) {
        return skillRegistry.get(skillId);
    }

    private final Map<SkillTrigger, List<Skill>> skillTriggerMap = new ConcurrentHashMap<>();

    public void registerSkill(Skill skill) {
        skillTriggerMap.computeIfAbsent(skill.trigger, _ -> new ArrayList<>()).add(skill);
    }

    public void triggerSkills(SkillTrigger trigger, SkillContext context) {
        List<Skill> skills = skillTriggerMap.get(trigger);
        if (skills == null || skills.isEmpty()) {
//            System.out.println("No skills registered for trigger: " + trigger);
            return;
        }

        for (Skill skill : skills) {
            if (skill.repeat < 1) {
                System.err.println("Invalid repeat value for skill: " + skill.skillId);
                continue;
            }

            if (skill.repeat == 1) {
                executeSkill(skill, context);
            } else {
                for (int i = 0; i < skill.repeat; i++) {
                    int delay = i * skill.repeatInterval;
                    SkillScheduler.scheduleSkillExecution(delay, () -> executeSkill(skill, context));
                }
            }
        }
    }

    private void executeSkill(Skill skill, SkillContext context) {
        if (context.hasPosition()) {
            skill.applyEffect(context.caster(), context.position());
        } else if (context.hasTarget()) {
            skill.applyEffect(context.caster(), context.target());
        } else {
            skill.applyEffect(context.caster());
        }
    }


    private final Map<Skill, TimerTask> scheduledTasks = new HashMap<>();

    private void scheduleTimerSkill(Skill skill) {
        Timer timer = new Timer(true);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                LivingEntity caster = getCaster();
                if (caster != null) {
                    skill.applyEffect(caster);
                }
            }
        };
        timer.scheduleAtFixedRate(task, skill.timer * 1000L, skill.timer * 1000L);
        scheduledTasks.put(skill, task);
    }

    public void unregisterSkill(Skill skill) {
        TimerTask task = scheduledTasks.remove(skill);
        if (task != null) {
            task.cancel();
        }
    }

    private LivingEntity getCaster() {
        // Implement your logic or throw an exception if the caster cannot be determined
        throw new UnsupportedOperationException("getCaster not implemented.");
    }
}
