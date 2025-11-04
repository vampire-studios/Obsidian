package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.github.vampirestudios.obsidian.api.crucible.Skill;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import net.minecraft.core.particles.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class ParticleSkill extends Skill {
    final ParticleOptions particleOptions;
    private final Optional<String> mob;
    private final int amount;
    private final double spread;
    private final double hSpread;
    private final double vSpread;
    private final double xSpread;
    private final double ySpread;
    private final double zSpread;
    final double speed;
    final double yOffset;
    private final int viewDistance;
    final boolean fromOrigin;
    private final boolean directional;
    private final boolean directionReversed;
    final Vec3 direction;
    private final int fixedYaw;
    private final int fixedPitch;
    private final Optional<Integer> color;
    private final Optional<Integer> color2;
    private final Optional<Integer> duration;
    private final Optional<String> block;
    private final Optional<String> item;
    final Optional<Vec3> targetPos;
    private final boolean exactOffsets;
    final Vec3 forwardOffset;
    final Vec3 sideOffset;

    public ParticleSkill(String skillId, SkillTarget<?> target, SkillTrigger trigger,
                         String particleTypeName, Optional<String> mob, int amount, double spread, double hSpread,
                         double vSpread, double xSpread, double ySpread, double zSpread, double speed,
                         double yOffset, int viewDistance, boolean fromOrigin, boolean directional,
                         boolean directionReversed, Vec3 direction, int fixedYaw, int fixedPitch, Optional<Integer> color,
                         Optional<Integer> color2, Optional<Integer> duration, Optional<String> blockName, Optional<String> itemName, Optional<Vec3> targetPos,
                         boolean exactOffsets, Vec3 forwardOffset, Vec3 sideOffset) {
        super(skillId, target, trigger);

        ParticleType<?> particleType = BuiltInRegistries.PARTICLE_TYPE.getValue(ResourceLocation.parse(particleTypeName));
        if (particleType == null) {
            throw new IllegalArgumentException("Unknown particle type: " + particleTypeName);
        }

        this.particleOptions = createParticleOptions(particleType);
        this.mob = mob;
        this.amount = amount;
        this.spread = spread;
        this.hSpread = hSpread;
        this.vSpread = vSpread;
        this.xSpread = xSpread;
        this.ySpread = ySpread;
        this.zSpread = zSpread;
        this.speed = speed;
        this.yOffset = yOffset;
        this.viewDistance = viewDistance;
        this.fromOrigin = fromOrigin;
        this.directional = directional;
        this.directionReversed = directionReversed;
        this.direction = direction;
        this.fixedYaw = fixedYaw;
        this.fixedPitch = fixedPitch;
        this.color = color;
        this.color2 = color2;
        this.duration = duration;
        this.block = blockName;
        this.item = itemName;
        this.targetPos = targetPos;
        this.exactOffsets = exactOffsets;
        this.forwardOffset = forwardOffset;
        this.sideOffset = sideOffset;
    }

    private ParticleOptions createParticleOptions(ParticleType<?> particleType) {
        try {
            if (particleType == ParticleTypes.DUST && color.isPresent()) {
                return new DustParticleOptions(color.get(), 1.0F);
            }
            if (particleType == ParticleTypes.DUST_COLOR_TRANSITION && color.isPresent() && color2.isPresent()) {
                return new DustColorTransitionOptions(color.get(), color2.get(), 1.0F);
            }
            if (particleType == ParticleTypes.ENTITY_EFFECT && color.isPresent() && color2.isPresent()) {
                return ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, color.get());
            }
            if (particleType == ParticleTypes.TRAIL && color.isPresent() && targetPos.isPresent() && duration.isPresent()) {
                return new TrailParticleOption(targetPos.get(), color.get(), duration.get());
            }
            if (isBlockParticleType(particleType) && block.isPresent()) {
                Block block = getBlockFromRegistry(this.block.get());
                return new BlockParticleOption((ParticleType<BlockParticleOption>) particleType, block.defaultBlockState());
            }
            if (particleType == ParticleTypes.ITEM && item.isPresent()) {
                ItemStack itemStack = new ItemStack(BuiltInRegistries.ITEM.getValue(ResourceLocation.parse(this.item.get())));
                return new ItemParticleOption(ParticleTypes.ITEM, itemStack);
            }
            return (ParticleOptions) particleType;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error creating particle options for particle type: " + particleType, e);
        }
    }

    // Helper method to check if the particle type is block-based
    private boolean isBlockParticleType(ParticleType<?> particleType) {
        return particleType == ParticleTypes.BLOCK ||
                particleType == ParticleTypes.BLOCK_MARKER ||
                particleType == ParticleTypes.BLOCK_CRUMBLE ||
                particleType == ParticleTypes.DUST_PILLAR ||
                particleType == ParticleTypes.FALLING_DUST;
    }

    // Helper method to retrieve Block from registry, with error handling
    private Block getBlockFromRegistry(String blockId) {
        ResourceLocation blockResource = ResourceLocation.tryParse(blockId);
        if (blockResource == null) {
            throw new IllegalArgumentException("Invalid block ID format: " + blockId);
        }
        Block block = BuiltInRegistries.BLOCK.getValue(blockResource);
        if (block == null) {
            throw new IllegalArgumentException("Unknown block ID: " + blockId);
        }
        return block;
    }

    @Override
    public void applyEffect(LivingEntity caster) {
        Vec3 originPosition = caster.position().add(0, yOffset, 0).add(forwardOffset).add(sideOffset);

        for (int i = 0; i < amount; i++) {
            Vec3 particlePosition = originPosition.add(
                    (xSpread != 0 ? xSpread : hSpread) * (Math.random() - 0.5),
                    (ySpread != 0 ? ySpread : vSpread) * (Math.random() - 0.5),
                    (zSpread != 0 ? zSpread : hSpread) * (Math.random() - 0.5)
            );

            Vec3 particleVelocity = directional ? direction.scale(directionReversed ? -1 : 1).scale(speed) : Vec3.ZERO;
            spawnAdvancedParticles(caster, particleOptions, particlePosition, particleVelocity);
        }
    }

    public void spawnAdvancedParticles(LivingEntity caster, ParticleOptions particleOptions, Vec3 position, Vec3 velocity) {
        Vec3 spawnPosition = position;

        // Apply fromOrigin and spread settings
        if (!fromOrigin) {
            spawnPosition = spawnPosition.add(
                    (Math.random() - 0.5) * spread,
                    (Math.random() - 0.5) * vSpread,
                    (Math.random() - 0.5) * hSpread
            );
        }

        // Apply exactOffsets if enabled
        if (exactOffsets) {
            spawnPosition = spawnPosition.add(forwardOffset).add(sideOffset);
        }

        // Apply viewDistance filter: Show particles only within the specified view distance
        Vec3 finalSpawnPosition = spawnPosition;
        caster.level().players().forEach(player -> {
            if (player.position().distanceTo(caster.position()) <= viewDistance) {
                spawnParticlesAtLocation(player, particleOptions, finalSpawnPosition, velocity);
            }
        });
    }

    private void spawnParticlesAtLocation(Player player, ParticleOptions options, Vec3 pos, Vec3 velocity) {
        player.level().addParticle(options, pos.x, pos.y, pos.z, velocity.x, velocity.y, velocity.z);
    }
}
