package io.github.vampirestudios.obsidian.api.crucible.targets.location;

import io.github.vampirestudios.obsidian.api.crucible.targets.LocationTarget;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomLocationsNearOriginTarget extends LocationTarget {
    private final int amount;
    private final double radius;
    private final double minRadius;
    private final double spacing;
    private final boolean onSurface;

    private static final Random RANDOM = new Random();

    public RandomLocationsNearOriginTarget(int amount, double radius, double minRadius, double spacing, boolean onSurface) {
        super(List.of("RandomLocationsNearOrigin", "RLO", "randomLocationsOrigin", "RLNO"));
        this.amount = amount;
        this.radius = radius;
        this.minRadius = minRadius;
        this.spacing = spacing;
        this.onSurface = onSurface;
    }

    @Override
    public List<Vec3> getTargets(LivingEntity caster) {
        List<Vec3> locations = new ArrayList<>();
        Level level = caster.level();
        Vec3 origin = caster.position();

        for (int i = 0; i < amount; i++) {
            Vec3 randomLocation;
            int attempts = 0;

            // Retry until we find a location meeting spacing requirements
            do {
                randomLocation = getRandomLocationNearOrigin(origin);
                attempts++;
            } while (isTooCloseToExisting(randomLocation, locations) && attempts < 10);

            if (attempts < 10) {
                // Optionally move to the surface above the nearest solid block
                if (onSurface) {
                    randomLocation = moveToSurface(level, randomLocation);
                }
                locations.add(randomLocation);
            }
        }
        return locations;
    }

    private Vec3 getRandomLocationNearOrigin(Vec3 origin) {
        double angle = RANDOM.nextDouble() * 2 * Math.PI;
        double distance = minRadius + RANDOM.nextDouble() * (radius - minRadius);
        double offsetX = distance * Math.cos(angle);
        double offsetZ = distance * Math.sin(angle);
        return origin.add(offsetX, 0, offsetZ);
    }

    private boolean isTooCloseToExisting(Vec3 location, List<Vec3> existingLocations) {
        for (Vec3 existing : existingLocations) {
            if (location.distanceTo(existing) < spacing) {
                return true;
            }
        }
        return false;
    }

    private Vec3 moveToSurface(Level level, Vec3 location) {
        BlockPos pos = new BlockPos((int) location.x, (int) location.y, (int) location.z);
        while (!level.getBlockState(pos).isSolid() && pos.getY() > level.getMinY()) {
            pos = pos.below();
        }
        return new Vec3(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
    }
}
