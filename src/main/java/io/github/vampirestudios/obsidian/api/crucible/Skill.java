package io.github.vampirestudios.obsidian.api.crucible;

import io.github.vampirestudios.obsidian.api.crucible.conditions.Condition;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Skill {
    public String skillId;
    public SkillTarget<?> target;
    public SkillTrigger trigger;
    public CrucibleSkill crucibleSkill;
    public int timer = -1; // Used for ON_TIMER trigger
    public List<Condition> Conditions;
    public List<Condition> TargetConditions;
    public List<Condition> TriggerConditions;
    protected Map<String, Object> parameters = new HashMap<>();
    protected Map<String, Object> runtimeParameters = new HashMap<>(); // Parameters passed at runtime
    int repeat = 1; // Default to 1, meaning it runs once if not specified
    int repeatInterval = 0; // Default to 0, meaning no interval if not specified

    public Skill() {}

    public Skill(String skillId, SkillTarget<?> target, SkillTrigger trigger) {
        this.skillId = skillId;
        this.target = target;
        this.trigger = trigger;
    }

    public void runSkill(LivingEntity caster, LivingEntity target) {
    }

    // Each specific skill type must implement its effect logic
    public void applyEffect(LivingEntity caster, LivingEntity target) {
        applyEffect(caster);
        if (target != null) applyEffect(caster, target.blockPosition());
    }

    private void scheduleSkillExecution(LivingEntity caster, LivingEntity target, int delay) {
        // This method should handle delayed execution. Use your scheduling framework here.
        // Example for Minecraft scheduler in Fabric or Bukkit:
        // server.getScheduler().schedule(this.plugin, () -> runSkillEffect(caster, target), delay, TimeUnit.TICKS);
    }

    // Core logic for executing the skill effect
    private void runSkillEffect(LivingEntity caster, LivingEntity target) {
        if (evaluateConditions(caster, target)) {
            applyEffect(caster, target);
        }
    }

	public void applyEffect(LivingEntity caster, BlockPos target) {

	}

	public void applyEffect(LivingEntity caster) {

	}

	// Method to provide aliases; overridden by subclasses if needed
    public List<String> aliases() {
        return List.of(); // Default is no aliases
    }

    public List<Condition> getConditions() {
        return Conditions;
    }

    public void setConditions(List<Condition> conditions) {
        Conditions = conditions;
    }

    public List<Condition> getTargetConditions() {
        return TargetConditions;
    }

    public void setTargetConditions(List<Condition> targetConditions) {
        TargetConditions = targetConditions;
    }

    public List<Condition> getTriggerConditions() {
        return TriggerConditions;
    }

    public void setTriggerConditions(List<Condition> triggerConditions) {
        TriggerConditions = triggerConditions;
    }

    // Method to evaluate all conditions for caster and target
    public boolean evaluateConditions(LivingEntity caster, LivingEntity target) {
        for (Condition condition : Conditions) {
            if (!condition.evaluate(caster, caster)) {
                return false;
            }
        }

        for (Condition targetCondition : TargetConditions) {
            if (!targetCondition.evaluate(caster, target)) {
                return false;
            }
        }
        return true;
    }

    // Getters and Setters for repeat and repeatInterval
    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public int getRepeatInterval() {
        return repeatInterval;
    }

    public void setRepeatInterval(int repeatInterval) {
        this.repeatInterval = repeatInterval;
    }
}