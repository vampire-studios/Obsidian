package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.BreathableComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.behaviour.LookAtPlayerBehaviourComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.behaviour.PanicBehaviourComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.behaviour.RandomLookAroundBehaviourComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.behaviour.TemptBehaviourComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.movement.BasicMovementComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityImpl extends PathAwareEntity {
    private final Entity entity;
    private final float health;
    private final Map<String, Component> components;
    private final BreathableComponent breathableComponent;

    public EntityImpl(EntityType<EntityImpl> type, World world, Entity entity, float health, BreathableComponent breathableComponent) {
        super(type, world);
        this.entity = entity;
        this.health = health;
        this.components = entity.components;
        this.breathableComponent = breathableComponent;
    }

    @Override
    public boolean canBreatheInWater() {
        return breathableComponent.breathes_water;
    }

    @Override
    public float getPathfindingFavor(BlockPos pos, WorldView world) {
        return super.getPathfindingFavor(pos, world);
    }

    @Override
    public boolean hasCustomName() {
        return entity.components.containsKey("minecraft:namable");
    }

    @Override
    public boolean isCustomNameVisible() {
        return entity.components.containsKey("minecraft:namable");
    }

    @Override
    public float getHealth() {
        return health;
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        if (entity == null) return;

        BasicMovementComponent basicMovementComponent = null;
        Component c = components.get("minecraft:behaviour.basic");
        if (c instanceof BasicMovementComponent basicMovementComponent1) {
            basicMovementComponent = basicMovementComponent1;
        }
        assert basicMovementComponent != null;
        this.goalSelector.add(1, new WanderAroundFarGoal(this, 1.0D));

        PanicBehaviourComponent panicBehaviourComponent = null;
        c = components.get("minecraft:behaviour.panic");
        if (c instanceof PanicBehaviourComponent panicBehaviourComponent1) {
            panicBehaviourComponent = panicBehaviourComponent1;
        }
        assert panicBehaviourComponent != null;
        this.goalSelector.add(panicBehaviourComponent.priority, new EscapeDangerGoal(this, panicBehaviourComponent.speed_multiplier));

        TemptBehaviourComponent temptBehaviourComponent = null;
        c = components.get("minecraft:behaviour.tempt");
        if (c instanceof TemptBehaviourComponent temptBehaviourComponent1) {
            temptBehaviourComponent = temptBehaviourComponent1;
        }
        assert temptBehaviourComponent != null;
        List<ItemStack> temptItems = new ArrayList<>();
        temptBehaviourComponent.items.forEach(item -> temptItems.add(new ItemStack(Registry.ITEM.get(Identifier.tryParse(item)))));
        this.goalSelector.add(temptBehaviourComponent.priority, new TemptGoal(this, temptBehaviourComponent.speed_multiplier, Ingredient.ofStacks(temptItems.stream()), temptBehaviourComponent.can_be_scared));

        RandomLookAroundBehaviourComponent randomLookAroundBehaviourComponent = null;
        c = components.get("minecraft:behaviour.random_look_around");
        if (c instanceof RandomLookAroundBehaviourComponent randomLookAroundBehaviourComponent1) {
            randomLookAroundBehaviourComponent = randomLookAroundBehaviourComponent1;
        }
        assert randomLookAroundBehaviourComponent != null;
        this.goalSelector.add(randomLookAroundBehaviourComponent.priority, new LookAroundGoal(this));

        LookAtPlayerBehaviourComponent lookAtPlayerBehaviourComponent = null;
        c = components.get("minecraft:behaviour.look_at_player");
        if (c instanceof LookAtPlayerBehaviourComponent lookAtPlayerBehaviourComponent1) {
            lookAtPlayerBehaviourComponent = lookAtPlayerBehaviourComponent1;
        }
        assert lookAtPlayerBehaviourComponent != null;
        this.goalSelector.add(lookAtPlayerBehaviourComponent.priority, new LookAtEntityGoal(this, PlayerEntity.class, lookAtPlayerBehaviourComponent.look_distance, lookAtPlayerBehaviourComponent.probability));
    }

}