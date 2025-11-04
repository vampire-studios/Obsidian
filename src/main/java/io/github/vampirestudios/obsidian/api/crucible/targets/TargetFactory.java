package io.github.vampirestudios.obsidian.api.crucible.targets;

import io.github.vampirestudios.obsidian.api.crucible.targets.entity.*;
import io.github.vampirestudios.obsidian.api.crucible.targets.location.*;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TargetFactory {
    private static final List<SkillTarget<?>> targets = new ArrayList<>();

    static {
        targets.add(new SelfTarget());
        targets.add(new NearestPlayerTarget(5));
        targets.add(new SelfLocationTarget());
        targets.add(new SelfEyeLocationTarget());
        targets.add(new PlayersInRadiusTarget(5));
        targets.add(new PlayersInRadiusLocationTarget(5));
        targets.add(new EntitiesInRingTarget(5, 10));
        targets.add(new PlayersInRingTarget(5, 10));
        targets.add(new EntitiesInRadiusTarget(5, true));
        targets.add(new ForwardTarget(5, 0, false, false, true, new Vec3(0, 0, 0)));
        targets.add(new TriggerTarget());
        targets.add(new OriginTarget());
        targets.add(new BlockVeinTarget("STONE", 10, true));
        targets.add(new TargetTarget());
        targets.add(new RandomLocationsNearOriginTarget(5, 5, 0, 0, false));  // default instance
        targets.add(new RandomLocationsNearCasterTarget(5, 5, 0, 0, false)); // default instance
        targets.add(new EntitiesInConeTarget(5, 45));              // Entities in a cone in front
        targets.add(new EntitiesInLineTarget(10, 2));              // Entities in a line
        targets.add(new RandomNearbyEntitiesTarget(3, 5));         // Random entities nearby
        targets.add(new EntitiesInRectangleTarget(3, 2, 6));       // Entities in a rectangle
        targets.add(new CircleAroundCasterTarget(4, 8, 1));        // Circle around caster
        targets.add(new RandomVerticalPointsTarget(5, 3, 8));      // Random vertical points
        targets.add(new LocationsAboveEntitiesTarget(5, 2, 3));    // Locations above entities
    }

    public static <T> SkillTarget<T> getTarget(String key, Map<String, String> parameters) {
        // Check for aliases and dynamic parameters
        for (SkillTarget<?> target : targets) {
            if (target.aliases.contains(key.toLowerCase())) {
				return switch (target) {
					case ForwardTarget _ -> (SkillTarget<T>) createForwardTarget(parameters);
					case EntitiesInRadiusTarget _ -> (SkillTarget<T>) createEntitiesInRadiusTarget(parameters);
					case PlayersInRadiusTarget _ -> (SkillTarget<T>) createPlayersInRadiusTarget(parameters);
					case BlockVeinTarget _ -> (SkillTarget<T>) createBlockVeinTarget(parameters);
                    case RandomLocationsNearOriginTarget _ -> (SkillTarget<T>) createRandomLocationsNearOriginTarget(parameters);
                    case RandomLocationsNearCasterTarget _ -> (SkillTarget<T>) createRandomLocationsNearCasterTarget(parameters);
                    case EntitiesInConeTarget _ -> (SkillTarget<T>) createEntitiesInConeTarget(parameters);
                    case EntitiesInLineTarget _ -> (SkillTarget<T>) createEntitiesInLineTarget(parameters);
                    case RandomNearbyEntitiesTarget _ -> (SkillTarget<T>) createRandomNearbyEntitiesTarget(parameters);
                    case EntitiesInRectangleTarget _ -> (SkillTarget<T>) createEntitiesInRectangleTarget(parameters);
                    case CircleAroundCasterTarget _ -> (SkillTarget<T>) createCircleAroundCasterTarget(parameters);
                    case RandomVerticalPointsTarget _ -> (SkillTarget<T>) createRandomVerticalPointsTarget(parameters);
                    case LocationsAboveEntitiesTarget _ -> (SkillTarget<T>) createLocationsAboveEntitiesTarget(parameters);
					default -> (SkillTarget<T>) target;
				};
			}
        }

        return null;
    }

    private static ForwardTarget createForwardTarget(Map<String, String> parameters) {
        double forward = Double.parseDouble(parameters.getOrDefault("forward", "5"));
        double rotate = Double.parseDouble(parameters.getOrDefault("rotate", "0"));
        boolean useEyeLocation = Boolean.parseBoolean(parameters.getOrDefault("useEyeLocation", "false"));
        boolean lockPitch = Boolean.parseBoolean(parameters.getOrDefault("lockPitch", "false"));
        boolean onSurface = Boolean.parseBoolean(parameters.getOrDefault("onSurface", "true"));

        double xOffset = Double.parseDouble(parameters.getOrDefault("xOffset", "0"));
        double yOffset = Double.parseDouble(parameters.getOrDefault("yOffset", "0"));
        double zOffset = Double.parseDouble(parameters.getOrDefault("zOffset", "0"));
        Vec3 offset = new Vec3(xOffset, yOffset, zOffset);

        return new ForwardTarget(forward, rotate, useEyeLocation, lockPitch, onSurface, offset);
    }

    // Custom configuration for EntitiesInRadiusTarget
    private static EntitiesInRadiusTarget createEntitiesInRadiusTarget(Map<String, String> parameters) {
        double radius = Double.parseDouble(parameters.getOrDefault("radius", "5"));
        boolean livingOnly = Boolean.parseBoolean(parameters.getOrDefault("livingOnly", "true"));
        return new EntitiesInRadiusTarget(radius, livingOnly);
    }

    // Custom configuration for PlayersInRadiusTarget
    private static PlayersInRadiusTarget createPlayersInRadiusTarget(Map<String, String> parameters) {
        double radius = Double.parseDouble(parameters.getOrDefault("radius", "5"));
        return new PlayersInRadiusTarget(radius);
    }

    // Custom configuration for BlockVeinTarget
    private static BlockVeinTarget createBlockVeinTarget(Map<String, String> parameters) {
        String blockType = parameters.getOrDefault("blockTypes", "STONE");
        int limit = Integer.parseInt(parameters.getOrDefault("limit", "10"));
        boolean originMustMatch = Boolean.parseBoolean(parameters.getOrDefault("originMustMatch", "true"));
        return new BlockVeinTarget(blockType, limit, originMustMatch);
    }

    private static RandomLocationsNearOriginTarget createRandomLocationsNearOriginTarget(Map<String, String> parameters) {
        int amount = Integer.parseInt(parameters.getOrDefault("amount", "5"));
        double radius = Double.parseDouble(parameters.getOrDefault("radius", "5"));
        double minRadius = Double.parseDouble(parameters.getOrDefault("minRadius", "0"));
        double spacing = Double.parseDouble(parameters.getOrDefault("spacing", "0"));
        boolean onSurface = Boolean.parseBoolean(parameters.getOrDefault("onSurface", "false"));

        return new RandomLocationsNearOriginTarget(amount, radius, minRadius, spacing, onSurface);
    }

    private static RandomLocationsNearCasterTarget createRandomLocationsNearCasterTarget(Map<String, String> parameters) {
        int amount = Integer.parseInt(parameters.getOrDefault("amount", "5"));
        double radius = Double.parseDouble(parameters.getOrDefault("radius", "5"));
        double minRadius = Double.parseDouble(parameters.getOrDefault("minRadius", "0"));
        double spacing = Double.parseDouble(parameters.getOrDefault("spacing", "0"));
        boolean onSurface = Boolean.parseBoolean(parameters.getOrDefault("onSurface", "false"));

        return new RandomLocationsNearCasterTarget(amount, radius, minRadius, spacing, onSurface);
    }

    private static EntitiesInConeTarget createEntitiesInConeTarget(Map<String, String> parameters) {
        double radius = Double.parseDouble(parameters.getOrDefault("radius", "5"));
        double angle = Double.parseDouble(parameters.getOrDefault("angle", "45"));
        return new EntitiesInConeTarget(radius, angle);
    }

    private static EntitiesInLineTarget createEntitiesInLineTarget(Map<String, String> parameters) {
        double length = Double.parseDouble(parameters.getOrDefault("length", "10"));
        double width = Double.parseDouble(parameters.getOrDefault("width", "2"));
        return new EntitiesInLineTarget(length, width);
    }

    private static RandomNearbyEntitiesTarget createRandomNearbyEntitiesTarget(Map<String, String> parameters) {
        int amount = Integer.parseInt(parameters.getOrDefault("amount", "3"));
        double radius = Double.parseDouble(parameters.getOrDefault("radius", "5"));
        return new RandomNearbyEntitiesTarget(amount, radius);
    }

    private static EntitiesInRectangleTarget createEntitiesInRectangleTarget(Map<String, String> parameters) {
        double width = Double.parseDouble(parameters.getOrDefault("width", "3"));
        double height = Double.parseDouble(parameters.getOrDefault("height", "2"));
        double length = Double.parseDouble(parameters.getOrDefault("length", "6"));
        return new EntitiesInRectangleTarget(width, height, length);
    }

    private static CircleAroundCasterTarget createCircleAroundCasterTarget(Map<String, String> parameters) {
        double radius = Double.parseDouble(parameters.getOrDefault("radius", "4"));
        int points = Integer.parseInt(parameters.getOrDefault("points", "8"));
        double height = Double.parseDouble(parameters.getOrDefault("height", "1"));
        return new CircleAroundCasterTarget(radius, points, height);
    }

    private static RandomVerticalPointsTarget createRandomVerticalPointsTarget(Map<String, String> parameters) {
        int amount = Integer.parseInt(parameters.getOrDefault("amount", "5"));
        double radius = Double.parseDouble(parameters.getOrDefault("radius", "3"));
        double height = Double.parseDouble(parameters.getOrDefault("height", "8"));
        return new RandomVerticalPointsTarget(amount, radius, height);
    }

    private static LocationsAboveEntitiesTarget createLocationsAboveEntitiesTarget(Map<String, String> parameters) {
        double radius = Double.parseDouble(parameters.getOrDefault("radius", "5"));
        double height = Double.parseDouble(parameters.getOrDefault("height", "2"));
        int amount = Integer.parseInt(parameters.getOrDefault("amount", "3"));
        return new LocationsAboveEntitiesTarget(radius, height, amount);
    }
}