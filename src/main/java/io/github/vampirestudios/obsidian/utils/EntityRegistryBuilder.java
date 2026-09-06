package io.github.vampirestudios.obsidian.utils;

import io.github.vampirestudios.obsidian.RegistryHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.SpawnEggItem;

public class EntityRegistryBuilder<E extends Entity> {
	private static Identifier name;
	private EntityFactory<E> entityFactory;
	private MobCategory category;
	private int trackingDistance;
	private int updateIntervalTicks;
	private boolean alwaysUpdateVelocity;
	private int primaryColor;
	private int secondaryColor;
	private boolean hasEgg;
	private boolean fireImmune;
	private boolean summonable;
	private EntityDimensions dimensions;

	public static <E extends Entity> EntityRegistryBuilder<E> createBuilder(Identifier nameIn) {
		name = nameIn;
		return new EntityRegistryBuilder<>();
	}

	public EntityRegistryBuilder<E> entity(EntityFactory<E> entityFactory) {
		this.entityFactory = entityFactory;
		return this;
	}

	public EntityRegistryBuilder<E> category(MobCategory category) {
		this.category = category;
		return this;
	}

	public EntityRegistryBuilder<E> tracker(int trackingDistance, int updateIntervalTicks, boolean alwaysUpdateVelocity) {
		this.trackingDistance = trackingDistance;
		this.updateIntervalTicks = updateIntervalTicks;
		this.alwaysUpdateVelocity = alwaysUpdateVelocity;
		return this;
	}

	public EntityRegistryBuilder<E> egg(int primaryColor, int secondaryColor) {
		this.primaryColor = primaryColor;
		this.secondaryColor = secondaryColor;
		return this;
	}

	public EntityRegistryBuilder<E> hasEgg(boolean hasEgg) {
		this.hasEgg = hasEgg;
		return this;
	}

	public EntityRegistryBuilder<E> makeFireImmune() {
		this.fireImmune = true;
		return this;
	}

	public EntityRegistryBuilder<E> summonable(boolean summonable) {
		this.summonable = summonable;
		return this;
	}

	public EntityRegistryBuilder<E> dimensions(EntityDimensions size) {
		this.dimensions = size;
		return this;
	}

	public EntityType<E> build() {
		EntityType.Builder<E> entityBuilder = EntityType.Builder.of(this.entityFactory, this.category)
				.sized(this.dimensions.width(), this.dimensions.height())
				.eyeHeight(this.dimensions.eyeHeight());
		if (fireImmune) {
			entityBuilder.fireImmune();
		}
		if (summonable) {
			entityBuilder.noSummon();
		}
		if (this.alwaysUpdateVelocity && this.updateIntervalTicks != 0 & this.trackingDistance != 0) {
			entityBuilder = EntityType.Builder.of(this.entityFactory, this.category)
					.sized(this.dimensions.width(), this.dimensions.height())
					.eyeHeight(this.dimensions.eyeHeight())
					.clientTrackingRange(this.trackingDistance)
					.updateInterval(this.updateIntervalTicks)
					.alwaysUpdateVelocity(this.alwaysUpdateVelocity);
		}

		EntityType<E> entityType;
		if (BuiltInRegistries.ENTITY_TYPE.containsKey(name)) {
			entityType = (EntityType<E>) BuiltInRegistries.ENTITY_TYPE.getValue(name);
		} else {
			entityType = Registry.register(BuiltInRegistries.ENTITY_TYPE, name, entityBuilder.build(ResourceKey.create(Registries.ENTITY_TYPE, name)));
		}

		if (this.hasEgg) {
			RegistryHelper.createRegistryHelper(name.getNamespace()).items().registerItem(String.format("%s_spawn_egg", name.getPath()), new SpawnEggItem(new Properties().spawnEgg(entityType).setId(ResourceKey.create(Registries.ITEM, name.withSuffix("_spawn_egg")))));
		}

		return entityType;
	}
}
