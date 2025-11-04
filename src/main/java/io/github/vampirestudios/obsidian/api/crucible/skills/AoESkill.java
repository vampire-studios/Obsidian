package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.github.vampirestudios.obsidian.api.crucible.Skill;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import io.github.vampirestudios.obsidian.api.crucible.skills.effects.Effect;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class AoESkill extends Skill {
    private final double radius;
    private final List<Effect> effects;

    public AoESkill(String skillId, SkillTarget<?> target, SkillTrigger trigger, double radius, List<Effect> effects) {
        super(skillId, target, trigger);
        this.radius = radius;
        this.effects = effects;
    }

    @Override
    public void applyEffect(LivingEntity caster, LivingEntity target) {
        List<LivingEntity> entities = caster.level().getEntitiesOfClass(
            LivingEntity.class, new AABB(target.blockPosition()).inflate(radius)
        );
        for (LivingEntity entity : entities) {
            for (Effect effect : effects) {
                effect.apply(entity);
            }
        }
    }
}
