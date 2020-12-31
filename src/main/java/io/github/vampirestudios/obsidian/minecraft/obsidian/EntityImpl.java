package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityImpl extends PathAwareEntity {
    private final Entity entity;
    private final float health;
    private final Map<String, Component> components;

    public EntityImpl(EntityType<EntityImpl> entityType_1, World world_1, Entity entity, float health) {
        super(entityType_1, world_1);
        this.entity = entity;
        this.health = health;
        this.components = entity.components;
    }

    @Override
    public boolean hasCustomName() {
        return entity.components.keySet().contains("minecraft:namable");
    }

    @Override
    public boolean isCustomNameVisible() {
        return entity.components.keySet().contains("minecraft:namable");
    }

    @Override
    public float getHealth() {
        return health;
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        if(entity == null) return;

        Component c = components.get("minecraft:movement.basic");
        if (c instanceof BasicMovementComponent) {
            this.goalSelector.add(1, new WanderAroundFarGoal(this, 1.0D));
        }

        PanicBehaviourComponent panicBehaviourComponent;
        c = components.get("minecraft:behaviour.panic");
        if (c instanceof PanicBehaviourComponent) {
            panicBehaviourComponent = (PanicBehaviourComponent) c;
            this.goalSelector.add(panicBehaviourComponent.priority, new EscapeDangerGoal(this, panicBehaviourComponent.speed_multiplier));
        }

        TemptBehaviourComponent temptBehaviourComponent;
        c = components.get("minecraft:behaviour.tempt");
        if (c instanceof TemptBehaviourComponent) {
            temptBehaviourComponent = (TemptBehaviourComponent) c;
            List<ItemStack> temptItems = new ArrayList<>();
            temptBehaviourComponent.items.forEach(item -> temptItems.add(new ItemStack(Registry.ITEM.get(Identifier.tryParse(item)))));
            this.goalSelector.add(temptBehaviourComponent.priority, new TemptGoal(this, temptBehaviourComponent.speed_multiplier, Ingredient.ofStacks(temptItems.stream()), temptBehaviourComponent.can_be_scared));
        }

        RandomLookAroundBehaviourComponent randomLookAroundBehaviourComponent;
        c = components.get("minecraft:behaviour.random_look_around");
        if (c instanceof RandomLookAroundBehaviourComponent) {
            randomLookAroundBehaviourComponent = (RandomLookAroundBehaviourComponent) c;
            this.goalSelector.add(randomLookAroundBehaviourComponent.priority, new LookAroundGoal(this));
        }

        LookAtPlayerBehaviourComponent lookAtPlayerBehaviourComponent;
        c = components.get("minecraft:behaviour.look_at_player");
        if (c instanceof LookAtPlayerBehaviourComponent) {
            lookAtPlayerBehaviourComponent = (LookAtPlayerBehaviourComponent) c;
            this.goalSelector.add(lookAtPlayerBehaviourComponent.priority, new LookAtEntityGoal(this, PlayerEntity.class, lookAtPlayerBehaviourComponent.look_distance, lookAtPlayerBehaviourComponent.probability));
        }
    }

}