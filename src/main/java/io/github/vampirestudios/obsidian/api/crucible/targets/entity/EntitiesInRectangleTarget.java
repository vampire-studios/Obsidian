package io.github.vampirestudios.obsidian.api.crucible.targets.entity;

import io.github.vampirestudios.obsidian.api.crucible.targets.EntityTarget;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class EntitiesInRectangleTarget extends EntityTarget<LivingEntity> {
	private final double width;
	private final double height;
	private final double length;

	public EntitiesInRectangleTarget(double width, double height, double length) {
		super(List.of("EntitiesInRectangle", "rectangle"));
		this.width = width;
		this.height = height;
		this.length = length;
	}

	@Override
	public List<LivingEntity> getTargets(LivingEntity caster) {
		Vec3 position = caster.position();
		AABB area = new AABB(position.x - width / 2, position.y, position.z - length / 2,
				position.x + width / 2, position.y + height, position.z + length / 2);
		return caster.level().getEntitiesOfClass(LivingEntity.class, area);
	}
}
