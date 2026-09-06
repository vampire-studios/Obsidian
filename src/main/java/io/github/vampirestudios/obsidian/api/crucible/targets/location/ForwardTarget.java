package io.github.vampirestudios.obsidian.api.crucible.targets.location;

import io.github.vampirestudios.obsidian.api.crucible.targets.LocationTarget;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;

import java.util.List;

public class ForwardTarget extends LocationTarget {
	private final double forward;
	private final double rotate;
	private final boolean useEyeLocation;
	private final boolean lockPitch;
	private final boolean onSurface;
	private final Vec3 offset;

	public ForwardTarget(double forward, double rotate, boolean useEyeLocation, boolean lockPitch, boolean onSurface, Vec3 offset) {
		super(List.of("Forward", "forward"));
		this.forward = forward;
		this.rotate = rotate;
		this.useEyeLocation = useEyeLocation;
		this.lockPitch = lockPitch;
		this.onSurface = onSurface;
		this.offset = offset;
	}

	@Override
	public List<Vec3> getTargets(LivingEntity caster) {
		Vec3 origin = useEyeLocation ? caster.getEyePosition() : caster.position();
		Vec3 direction = calculateDirection(caster);

		// Apply forward distance and rotation
		Vec3 targetPosition = origin.add(direction.scale(forward));
		targetPosition = applyRotation(targetPosition, caster, rotate);

		// Apply offsets
		targetPosition = targetPosition.add(offset);

		// Adjust to surface if onSurface is true
		if (onSurface) {
			targetPosition = alignToSurface(caster.level(), targetPosition);
		}

		return List.of(targetPosition);
	}

	private Vec3 calculateDirection(LivingEntity caster) {
		// Lock pitch to 0 if lockPitch is true, or use caster’s pitch otherwise
		double pitch = lockPitch ? 0 : caster.getXRot();
		double yaw = caster.getYRot();

		double radPitch = Math.toRadians(pitch);
		double radYaw = Math.toRadians(yaw);

		double x = -Math.cos(radPitch) * Math.sin(radYaw);
		double y = -Math.sin(radPitch);
		double z = Math.cos(radPitch) * Math.cos(radYaw);

		return new Vec3(x, y, z);
	}

	private Vec3 applyRotation(Vec3 position, LivingEntity caster, double rotate) {
		double yaw = Math.toRadians(caster.getYRot() + rotate);

		double cosYaw = Math.cos(yaw);
		double sinYaw = Math.sin(yaw);

		double x = position.x * cosYaw - position.z * sinYaw;
		double z = position.x * sinYaw + position.z * cosYaw;

		return new Vec3(x, position.y, z);
	}

	private Vec3 alignToSurface(Level world, Vec3 position) {
		BlockHitResult hitResult = world.clip(new ClipContext(
				position, position.add(0, -1, 0), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));

		if (hitResult.getType() == HitResult.Type.BLOCK) {
			return hitResult.getLocation();
		}

		return position; // If no surface is found, return the original position
	}
}