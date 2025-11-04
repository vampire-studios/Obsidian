package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.github.vampirestudios.obsidian.api.crucible.Skill;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import io.github.vampirestudios.obsidian.api.crucible.skills.effects.Effect;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ProjectileSkill extends Skill {
    private final EntityType<?> projectileType;
    private final float speed;
    private final List<Effect> onImpactEffects;

    public ProjectileSkill(String skillId, SkillTarget<?> target, SkillTrigger trigger, EntityType<?> projectileType, float speed, List<Effect> onImpactEffects) {
        super(skillId, target, trigger);
        this.projectileType = projectileType;
        this.speed = speed;
        this.onImpactEffects = onImpactEffects;
    }

    @Override
    public void applyEffect(LivingEntity caster, LivingEntity target) {
        Entity projectile = projectileType.create(caster.level(), EntitySpawnReason.SPAWN_ITEM_USE);
        projectile.setPos(caster.getX(), caster.getY() + caster.getEyeHeight(), caster.getZ());
        Vec3 direction = target.position().subtract(caster.position()).normalize().scale(speed);
        projectile.setDeltaMovement(direction);

        // On impact, apply effects
        /*projectile.updateDynamicGameEventListener((event) -> {
            if (event instanceof EntityHitResult) {
                LivingEntity hitEntity = ((EntityHitResult) event).getEntity();
                for (Effect effect : onImpactEffects) {
                    effect.apply(hitEntity);
                }
            }
        });
*/
        caster.level().addFreshEntity(projectile);
    }
}
