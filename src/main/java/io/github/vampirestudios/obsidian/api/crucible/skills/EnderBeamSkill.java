package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.github.vampirestudios.obsidian.api.crucible.Skill;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;

import java.util.Objects;

public class EnderBeamSkill extends Skill {
    private final int duration;
    private final double yOffset;

    public EnderBeamSkill(String skillId, SkillTarget<?> target, SkillTrigger trigger, int duration, double yOffset) {
        super(skillId, target, trigger);
        this.duration = duration;
        this.yOffset = yOffset;
    }

    @Override
    public void applyEffect(LivingEntity caster, LivingEntity target) {
        new Animator(caster, target, this.duration);
    }

    @Override
    public void applyEffect(LivingEntity caster, BlockPos target) {
        new Animator(caster, target, this.duration);
    }

    private class Animator implements Runnable {
        private EndCrystal crystal;
        private BlockPos locationSource;
        private Entity entitySource;
        private Entity entity;
        private BlockPos location;
        private int interval = 1;
        private int duration;
        private int iteration;
//        private Task taskId;

        public Animator(Entity source, BlockPos location, int duration) {
            this.entitySource = source;
            this.location = location;
            this.start(duration);
        }

        public Animator(Entity source, Entity entity, int duration) {
            this.entitySource = source;
            this.entity = entity;
            this.start(duration);
        }

        public Animator(BlockPos source, BlockPos location, int duration) {
            this.locationSource = source;
            this.location = location;
            this.start(duration);
        }

        public Animator(BlockPos source, Entity entity, int duration) {
            this.locationSource = source;
            this.entity = entity;
            this.start(duration);
        }

        protected void start(int duration) {
            this.crystal = EntityTypes.END_CRYSTAL.create(this.entitySource.level(), EntitySpawnReason.SPAWN_ITEM_USE);
			assert this.crystal != null;
			this.crystal.setShowBottom(false);
            this.crystal.setInvulnerable(true);
            this.duration = duration;
            this.iteration = 0;
//            this.taskId = Schedulers.sync().runRepeating(this, 0L, (long)this.interval);
        }

        public void run() {
            if (this.iteration > this.duration) {
                this.crystal.remove(Entity.RemovalReason.DISCARDED);
//                this.taskId.terminate();
            } else {
                BlockPos source;
				source = Objects.requireNonNullElseGet(this.locationSource, () -> this.entitySource.blockPosition());

                source = source.offset(0, (int) EnderBeamSkill.this.yOffset, 0);
                BlockPos target;
				target = Objects.requireNonNullElseGet(this.location, () -> this.entity.blockPosition());

                this.crystal.teleportTo(source.getX(), source.getY(), source.getZ());
                this.crystal.setBeamTarget(target);
                ++this.iteration;
            }

        }
    }
}
