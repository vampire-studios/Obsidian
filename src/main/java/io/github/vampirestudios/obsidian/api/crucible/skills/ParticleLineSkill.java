package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class ParticleLineSkill extends ParticleSkill {
    private final double distanceBetween;
    private final double startYOffset;
    private final double targetYOffset;
    private final boolean zigzag;
    private final int zigzags;
    private final double zigzagOffset;
    private final double maxDistance;

    public ParticleLineSkill(
            String skillId, SkillTarget<?> target, SkillTrigger trigger, String particleTypeName,
            Optional<String> mob, int amount, double spread, double hSpread, double vSpread,
            double xSpread, double ySpread, double zSpread, double speed, double yOffset, int viewDistance,
            boolean fromOrigin, boolean directional, boolean directionReversed, Vec3 direction, int fixedYaw,
            int fixedPitch, Optional<Integer> color, Optional<Integer> color2, Optional<Integer> duration,
            Optional<String> blockName, Optional<String> itemName, Optional<Vec3> targetPos, boolean exactOffsets,
            Vec3 forwardOffset, Vec3 sideOffset, double distanceBetween, double startYOffset, double targetYOffset,
            boolean zigzag, int zigzags, double zigzagOffset, double maxDistance) {
        super(skillId, target, trigger, particleTypeName, mob, amount, spread, hSpread, vSpread, xSpread,
                ySpread, zSpread, speed, yOffset, viewDistance, fromOrigin, directional, directionReversed,
                direction, fixedYaw, fixedPitch, color, color2, duration, blockName, itemName, targetPos, exactOffsets,
                forwardOffset, sideOffset);
        this.distanceBetween = distanceBetween;
        this.startYOffset = startYOffset;
        this.targetYOffset = targetYOffset;
        this.zigzag = zigzag;
        this.zigzags = zigzags;
        this.zigzagOffset = zigzagOffset;
        this.maxDistance = maxDistance;
    }

    @Override
    public void applyEffect(LivingEntity caster) {
        Vec3 start = caster.position().add(0, yOffset + startYOffset, 0).add(forwardOffset).add(sideOffset);
        Vec3 end = targetPos.orElseGet(() -> start.add(direction.scale(maxDistance)));

        if (targetPos.isPresent()) {
            end = targetPos.get().add(0, targetYOffset, 0);
        } else if (fromOrigin) {
            end = start.add(direction.scale(maxDistance)).add(0, targetYOffset, 0);
        }

        Vec3 lineDirection = end.subtract(start).normalize();
        double distance = Math.min(start.distanceTo(end), maxDistance);
        int pointsCount = (int) (distance / distanceBetween);

        for (int i = 0; i <= pointsCount; i++) {
            double offset = zigzag ? Math.sin((double) i / pointsCount * Math.PI * zigzags) * zigzagOffset : 0;
            Vec3 point = start.add(lineDirection.scale(i * distanceBetween)).add(0, offset, 0);
            spawnAdvancedParticles(caster, particleOptions, point, lineDirection.scale(speed));
        }
    }
}
