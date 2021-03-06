//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.github.vampirestudios.obsidian.utils;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.EntityFactory;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item.Settings;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityRegistryBuilder<E extends Entity> {
    private static Identifier name;
    private EntityFactory<E> entityFactory;
    private SpawnGroup category;
    private int trackingDistance;
    private int updateIntervalTicks;
    private boolean alwaysUpdateVelocity;
    private int primaryColor;
    private int secondaryColor;
    private boolean hasEgg;
    private boolean fireImmune;
    private boolean summonable;
    private EntityDimensions dimensions;

    public EntityRegistryBuilder() {
    }

    public static <E extends Entity> EntityRegistryBuilder<E> createBuilder(Identifier nameIn) {
        name = nameIn;
        return new EntityRegistryBuilder<>();
    }

    public EntityRegistryBuilder<E> entity(EntityFactory<E> entityFactory) {
        this.entityFactory = entityFactory;
        return this;
    }

    public EntityRegistryBuilder<E> category(SpawnGroup category) {
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
        FabricEntityTypeBuilder<E> entityBuilder = FabricEntityTypeBuilder.create(this.category, this.entityFactory).dimensions(this.dimensions);
        if (fireImmune) {
            entityBuilder.fireImmune();
        }
        if (summonable) {
            entityBuilder.disableSummon();
        }
        if (this.alwaysUpdateVelocity && this.updateIntervalTicks != 0 & this.trackingDistance != 0) {
            entityBuilder = FabricEntityTypeBuilder.create(this.category, this.entityFactory).dimensions(this.dimensions)
                    .trackable(this.trackingDistance, this.updateIntervalTicks, this.alwaysUpdateVelocity);
        }

        EntityType<E> entityType;
        if (Registry.ENTITY_TYPE.containsId(name)) {
            entityType = (EntityType<E>) Registry.ENTITY_TYPE.get(name);
        } else {
            entityType = Registry.register(Registry.ENTITY_TYPE, name, entityBuilder.build());
        }

        if (this.hasEgg) {
            RegistryUtils.registerItem(new SpawnEggItem((EntityType<? extends MobEntity>) entityType, this.primaryColor, this.secondaryColor, (new Settings()).group(ItemGroup.MISC)), new Identifier(name.getNamespace(), String.format("%s_spawn_egg", name.getPath())));
        }

        return entityType;
    }
}
