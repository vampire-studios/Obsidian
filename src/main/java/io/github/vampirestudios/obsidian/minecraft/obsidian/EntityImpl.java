package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Entity;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.BreathableComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.behaviour.LookAtPlayerBehaviourComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.behaviour.PanicBehaviourComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.behaviour.RandomLookAroundBehaviourComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.behaviour.TemptBehaviourComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.movement.BasicMovementComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityImpl extends PathfinderMob {
    private final Entity entity;
    private final float health;
    private final Map<String, Component> components;
    private final BreathableComponent breathableComponent;
    public final Map<AnimationState, ResourceLocation> animationStates = new HashMap<>();

    public EntityImpl(EntityType<EntityImpl> type, Level world, Entity entity, float health, BreathableComponent breathableComponent) {
        super(type, world);
        this.entity = entity;
        this.health = health;
        this.components = entity.components;
        this.breathableComponent = breathableComponent;
        this.entity.animations.forEach(identifier -> animationStates.put(new AnimationState(), identifier));
    }

    @Override
    public boolean canBreatheUnderwater() {
        return breathableComponent.breathes_water;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader world) {
        return super.getWalkTargetValue(pos, world);
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
    protected void registerGoals() {
        super.registerGoals();
        if (entity == null) return;

        BasicMovementComponent basicMovementComponent = null;
        Component c = components.get("minecraft:behaviour.basic");
        if (c instanceof BasicMovementComponent basicMovementComponent1) {
            basicMovementComponent = basicMovementComponent1;
        }
        assert basicMovementComponent != null;
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.0D));

        PanicBehaviourComponent panicBehaviourComponent = null;
        c = components.get("minecraft:behaviour.panic");
        if (c instanceof PanicBehaviourComponent panicBehaviourComponent1) {
            panicBehaviourComponent = panicBehaviourComponent1;
        }
        assert panicBehaviourComponent != null;
        this.goalSelector.addGoal(panicBehaviourComponent.priority, new PanicGoal(this, panicBehaviourComponent.speed_multiplier));

        TemptBehaviourComponent temptBehaviourComponent = null;
        c = components.get("minecraft:behaviour.tempt");
        if (c instanceof TemptBehaviourComponent temptBehaviourComponent1) {
            temptBehaviourComponent = temptBehaviourComponent1;
        }
        assert temptBehaviourComponent != null;
        List<ItemStack> temptItems = new ArrayList<>();
        temptBehaviourComponent.items.forEach(item -> temptItems.add(new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.tryParse(item)))));
        this.goalSelector.addGoal(temptBehaviourComponent.priority, new TemptGoal(this, temptBehaviourComponent.speed_multiplier, Ingredient.of(temptItems.stream()), temptBehaviourComponent.can_be_scared));

        RandomLookAroundBehaviourComponent randomLookAroundBehaviourComponent = null;
        c = components.get("minecraft:behaviour.random_look_around");
        if (c instanceof RandomLookAroundBehaviourComponent randomLookAroundBehaviourComponent1) {
            randomLookAroundBehaviourComponent = randomLookAroundBehaviourComponent1;
        }
        assert randomLookAroundBehaviourComponent != null;
        this.goalSelector.addGoal(randomLookAroundBehaviourComponent.priority, new RandomLookAroundGoal(this));

        LookAtPlayerBehaviourComponent lookAtPlayerBehaviourComponent = null;
        c = components.get("minecraft:behaviour.look_at_player");
        if (c instanceof LookAtPlayerBehaviourComponent lookAtPlayerBehaviourComponent1) {
            lookAtPlayerBehaviourComponent = lookAtPlayerBehaviourComponent1;
        }
        assert lookAtPlayerBehaviourComponent != null;
        this.goalSelector.addGoal(lookAtPlayerBehaviourComponent.priority, new LookAtPlayerGoal(this, Player.class, lookAtPlayerBehaviourComponent.look_distance, lookAtPlayerBehaviourComponent.probability));
    }

}