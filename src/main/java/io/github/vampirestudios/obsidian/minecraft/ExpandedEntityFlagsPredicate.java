package io.github.vampirestudios.obsidian.minecraft;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public class ExpandedEntityFlagsPredicate {
	public static final ExpandedEntityFlagsPredicate ANY = new ExpandedEntityFlagsPredicate.Builder().build();
	@Nullable
	private final Boolean isOnFire;
	@Nullable
	private final Boolean isCrouching;
	@Nullable
	private final Boolean isSprinting;
	@Nullable
	private final Boolean isSwimming;
	@Nullable
	private final Boolean isWalking;
	@Nullable
	private final Boolean isBaby;

	public ExpandedEntityFlagsPredicate(
		@Nullable Boolean isOnFire, @Nullable Boolean isCrouching, @Nullable Boolean isSprinting, @Nullable Boolean isSwimming, @Nullable Boolean isWalking,
		@Nullable Boolean isBaby

	) {
		this.isOnFire = isOnFire;
		this.isCrouching = isCrouching;
		this.isSprinting = isSprinting;
		this.isSwimming = isSwimming;
		this.isWalking = isWalking;
		this.isBaby = isBaby;
	}

	public boolean matches(Entity entity) {
		if (this.isOnFire != null && entity.isOnFire() != this.isOnFire) {
			return false;
		} else if (this.isCrouching != null && entity.isCrouching() != this.isCrouching) {
			return false;
		} else if (this.isSprinting != null && entity.isSprinting() != this.isSprinting) {
			return false;
		} else if (this.isSwimming != null && entity.isSwimming() != this.isSwimming) {
			return false;
		} else if (this.isWalking != null && entity.isSwimming() != this.isWalking) {
			return false;
		} else {
			return this.isBaby == null || !(entity instanceof LivingEntity) || ((LivingEntity)entity).isBaby() == this.isBaby;
		}
	}

	@Nullable
	private static Boolean getOptionalBoolean(JsonObject json, String name) {
		return json.has(name) ? GsonHelper.getAsBoolean(json, name) : null;
	}

	public static ExpandedEntityFlagsPredicate fromJson(@Nullable JsonElement json) {
		if (json != null && !json.isJsonNull()) {
			JsonObject jsonObject = GsonHelper.convertToJsonObject(json, "entity flags");
			Boolean boolean_ = getOptionalBoolean(jsonObject, "is_on_fire");
			Boolean boolean2 = getOptionalBoolean(jsonObject, "is_sneaking");
			Boolean boolean3 = getOptionalBoolean(jsonObject, "is_sprinting");
			Boolean boolean4 = getOptionalBoolean(jsonObject, "is_swimming");
			Boolean boolean5 = getOptionalBoolean(jsonObject, "is_walking");
			Boolean boolean6 = getOptionalBoolean(jsonObject, "is_baby");
			return new ExpandedEntityFlagsPredicate(boolean_, boolean2, boolean3, boolean4, boolean5, boolean6);
		} else {
			return ANY;
		}
	}

	private void addOptionalBoolean(JsonObject json, String name, @Nullable Boolean value) {
		if (value != null) {
			json.addProperty(name, value);
		}
	}

	public JsonElement serializeToJson() {
		if (this == ANY) {
			return JsonNull.INSTANCE;
		} else {
			JsonObject jsonObject = new JsonObject();
			this.addOptionalBoolean(jsonObject, "is_on_fire", this.isOnFire);
			this.addOptionalBoolean(jsonObject, "is_sneaking", this.isCrouching);
			this.addOptionalBoolean(jsonObject, "is_sprinting", this.isSprinting);
			this.addOptionalBoolean(jsonObject, "is_swimming", this.isSwimming);
			this.addOptionalBoolean(jsonObject, "is_walking", this.isWalking);
			this.addOptionalBoolean(jsonObject, "is_baby", this.isBaby);
			return jsonObject;
		}
	}

	public static class Builder {
		@Nullable
		private Boolean isOnFire;
		@Nullable
		private Boolean isCrouching;
		@Nullable
		private Boolean isSprinting;
		@Nullable
		private Boolean isSwimming;
		@Nullable
		private Boolean isWalking;
		@Nullable
		private Boolean isBaby;

		public static ExpandedEntityFlagsPredicate.Builder flags() {
			return new ExpandedEntityFlagsPredicate.Builder();
		}

		public ExpandedEntityFlagsPredicate.Builder setOnFire(@Nullable Boolean onFire) {
			this.isOnFire = onFire;
			return this;
		}

		public ExpandedEntityFlagsPredicate.Builder setCrouching(@Nullable Boolean isCrouching) {
			this.isCrouching = isCrouching;
			return this;
		}

		public ExpandedEntityFlagsPredicate.Builder setSprinting(@Nullable Boolean isSprinting) {
			this.isSprinting = isSprinting;
			return this;
		}

		public ExpandedEntityFlagsPredicate.Builder setSwimming(@Nullable Boolean isSwimming) {
			this.isSwimming = isSwimming;
			return this;
		}

		public ExpandedEntityFlagsPredicate.Builder setWalking(@Nullable Boolean walking) {
			this.isWalking = walking;
			return this;
		}

		public ExpandedEntityFlagsPredicate.Builder setIsBaby(@Nullable Boolean isBaby) {
			this.isBaby = isBaby;
			return this;
		}

		public ExpandedEntityFlagsPredicate build() {
			return new ExpandedEntityFlagsPredicate(this.isOnFire, this.isCrouching, this.isSprinting, this.isSwimming, this.isWalking, this.isBaby);
		}
	}
}
