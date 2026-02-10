package io.github.vampirestudios.obsidian.api.crucible;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;


public final class SkillContext {
	public final LivingEntity caster;
	public final @Nullable LivingEntity target;
	public final @Nullable BlockPos position;

	public final @Nullable ServerLevel level;

	public final @Nullable ItemStack stack;
	public final @Nullable InteractionHand hand;

	public final @Nullable Entity projectile;

	private SkillContext(Builder b) {
		this.caster = b.caster;
		this.target = b.target;
		this.position = b.position;
		this.level = b.level;
		this.stack = b.stack;
		this.hand = b.hand;
		this.projectile = b.projectile;
	}

	public boolean hasTarget()   { return target != null; }
	public boolean hasPosition() { return position != null; }
	public boolean hasStack()    { return stack != null && !stack.isEmpty(); }

	public static Builder builder(LivingEntity caster) { return new Builder(caster); }

	public static final class Builder {
		private final LivingEntity caster;
		private LivingEntity target;
		private BlockPos position;
		private ServerLevel level;
		private ItemStack stack;
		private InteractionHand hand;
		private Entity projectile;

		private Builder(LivingEntity caster) { this.caster = caster; }

		public Builder target(@Nullable LivingEntity v) { this.target = v; return this; }
		public Builder position(@Nullable BlockPos v)   { this.position = v; return this; }
		public Builder level(@Nullable ServerLevel v)   { this.level = v; return this; }
		public Builder stack(@Nullable ItemStack v)     { this.stack = v; return this; }
		public Builder hand(@Nullable InteractionHand v){ this.hand = v; return this; }
		public Builder projectile(@Nullable Entity v)   { this.projectile = v; return this; }

		public SkillContext build() { return new SkillContext(this); }
	}
}