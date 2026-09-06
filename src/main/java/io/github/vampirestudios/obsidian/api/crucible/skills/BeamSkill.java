package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.github.vampirestudios.obsidian.api.crucible.Skill;
import io.github.vampirestudios.obsidian.api.crucible.SkillTrigger;
import io.github.vampirestudios.obsidian.api.crucible.targets.SkillTarget;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.NameAndId;
import net.minecraft.server.players.UserBanListEntry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class BeamSkill extends Skill {
	private final double distance;
	private final Optional<Integer> beamColor;
	private final Optional<String> item;
	private final Optional<Integer> modelData;
	private final Optional<String> action;
	private final Optional<String> reason;
	private final Optional<Float> amount;

	public BeamSkill(String skillId, SkillTarget<?> target, SkillTrigger trigger,
	                 double distance, Optional<Integer> beamColor, Optional<String> item, Optional<Integer> modelData,
	                 Optional<String> action, Optional<String> reason, Optional<Float> amount) {
		super(skillId, target, trigger);
		this.distance = distance;
		this.beamColor = beamColor;
		this.item = item;
		this.modelData = modelData;
		this.action = action;
		this.reason = reason;
		this.amount = amount;
	}

	@Override
	public void applyEffect(LivingEntity caster) {
		if (!(caster.level() instanceof ServerLevel level)) return;

		Vec3 origin = caster.getEyePosition();
		Vec3 direction = caster.getLookAngle();
		Vec3 targetPosition = origin.add(direction.scale(distance));

		drawBeam(level, origin, targetPosition);

		LivingEntity target = findTarget(level, origin, direction);
		if (target != null) {
			performAction(caster, target);
		}
	}

	private void performAction(LivingEntity caster, LivingEntity target) {
		String actionType = action.orElse("none").toLowerCase();

		switch (actionType) {
			case "ban":
				if (target instanceof ServerPlayer player) {
					banPlayer(player);
				}
				break;

			case "damage":
				target.hurt(caster.damageSources().magic(), amount.orElse(10F));
				break;

			case "heal":
				if (target instanceof LivingEntity livingTarget) {
					livingTarget.heal(amount.orElse(5F));
				}
				break;

			case "teleport":
				if (caster instanceof ServerPlayer player) {
					player.teleportTo(target.getX(), target.getY(), target.getZ());
				}
				break;

			case "effect":
				if (target instanceof LivingEntity livingTarget) {
					applyEffectToTarget(livingTarget);
				}
				break;

			default:
				System.out.println("Unknown action: " + actionType);
				break;
		}
	}

	private void banPlayer(ServerPlayer player) {
		String banReason = reason.orElse("You have been permanently banned.");
		Component reasonComponent = Component.literal(banReason);

		player.connection.disconnect(reasonComponent);
		player.level().getServer().getPlayerList().getBans().add(new UserBanListEntry(new NameAndId(player.getGameProfile()), null, "Server Admin", null, banReason));
	}

	private void applyEffectToTarget(LivingEntity target) {
		MobEffectInstance effect = new MobEffectInstance(MobEffects.LEVITATION, 200, 1);
		target.addEffect(effect);
	}

	private void drawBeam(ServerLevel level, Vec3 start, Vec3 end) {
		if (item.isPresent()) {
			renderBeamTexture(level, start, end, item.get(), modelData, beamColor);
		} else {
			renderBeam(level, start, end, beamColor);
		}
	}

	public void renderBeam(ServerLevel level, Vec3 start, Vec3 end, Optional<Integer> color) {
		Vec3 direction = end.subtract(start).normalize();
		double segmentLength = 0.5; // Distance between particles
		double distance = start.distanceTo(end);

		if (color.isPresent()) {
			DustParticleOptions particleOptions = new DustParticleOptions(color.get(), 1.0F);
			for (double d = 0; d <= distance; d += segmentLength) {
				Vec3 position = start.add(direction.scale(d));
				level.sendParticles(particleOptions, position.x, position.y, position.z, 1, 0, 0, 0, 0);
			}
		} else {
			for (double d = 0; d <= distance; d += segmentLength) {
				Vec3 currentPoint = start.add(direction.scale(d));
				level.sendParticles(ParticleTypes.END_ROD, currentPoint.x, currentPoint.y, currentPoint.z, 1, 0, 0, 0, 0.0);
			}
		}
	}

	private void renderBeamTexture(ServerLevel level, Vec3 start, Vec3 end, String itemId, Optional<Integer> modelData, Optional<Integer> beamColor) {
		// Divide the beam into smaller segments for rendering
		Vec3 direction = end.subtract(start).normalize();
		double distance = start.distanceTo(end);
		double segmentLength = 0.5; // Length of each segment

		for (double d = 0; d <= distance; d += segmentLength) {
			Vec3 segmentPosition = start.add(direction.scale(d));

			// Spawn an item display entity at the segment position
			ItemStack beamItem = createBeamItem(itemId, modelData, beamColor);
			spawnItemDisplayEntity(level, segmentPosition, beamItem);
		}
	}

	private ItemStack createBeamItem(String itemId, Optional<Integer> modelData, Optional<Integer> beamColor) {
		Identifier identifier = Identifier.tryParse(itemId);
		if (identifier == null) {
			throw new IllegalArgumentException("Invalid item ID: " + itemId);
		}
		ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.getValue(identifier));
		modelData.ifPresent(value -> stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(List.of(), List.of(), List.of(), List.of(value))));
		beamColor.ifPresent(color -> stack.set(DataComponents.DYED_COLOR, new DyedItemColor(color)));
		return stack;
	}

	private void spawnItemDisplayEntity(ServerLevel level, Vec3 position, ItemStack itemStack) {
		Display.ItemDisplay displayEntity = new Display.ItemDisplay(EntityTypes.ITEM_DISPLAY, level);
		displayEntity.setPos(position.x, position.y, position.z);
		displayEntity.setItemStack(itemStack);
		displayEntity.setNoGravity(true);

		level.addFreshEntity(displayEntity);

		// Schedule removal after a delay
//        level.getServer().schedule(new TickTask(60, () -> displayEntity.remove(Entity.RemovalReason.DISCARDED)));
	}

	private LivingEntity findTarget(ServerLevel level, Vec3 origin, Vec3 direction) {
		return level.getEntitiesOfClass(LivingEntity.class, new AABB(origin, origin.add(direction.scale(distance))))
				.stream().findFirst().orElse(null);
	}
}
