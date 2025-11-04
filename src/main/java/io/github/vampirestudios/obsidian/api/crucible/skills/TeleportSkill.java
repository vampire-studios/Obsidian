package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.github.vampirestudios.obsidian.api.crucible.Skill;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class TeleportSkill extends Skill {
    private final double maxDistance;
    private final boolean random;

    public TeleportSkill(String skillId, SkillTarget<?> target, SkillTrigger trigger, double maxDistance, boolean random) {
        super(skillId, target, trigger);
        this.maxDistance = maxDistance;
        this.random = random;
    }

    @Override
    public void applyEffect(LivingEntity caster, LivingEntity target) {
        Vec3 destination;
        if (random) {
            double xOffset = (Math.random() - 0.5) * maxDistance;
            double zOffset = (Math.random() - 0.5) * maxDistance;
            destination = new Vec3(caster.getX() + xOffset, caster.getY(), caster.getZ() + zOffset);
        } else {
            destination = target.position();
        }
        caster.teleportTo(destination.x, destination.y, destination.z);
    }
}
