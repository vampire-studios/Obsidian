package io.github.vampirestudios.obsidian.api.fabric;

import io.github.vampirestudios.obsidian.mixins.TridentEntityAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * This is the default implementation for the FabricTridentEntity, allowing for the easy creation of new Trident Entities in Fabric.
 */
public class SimpleTridentItemEntity extends TridentEntity {

    public SimpleTridentItemEntity(EntityType<? extends TridentEntity> entityType, World world, ItemStack tridentStack) {
        super(entityType, world);
        ((TridentEntityAccessor) this).setTridentStack(tridentStack);
    }

    public SimpleTridentItemEntity(World world, LivingEntity owner, ItemStack stack) {
        super(world, owner, stack);
        ((TridentEntityAccessor) this).setTridentStack(stack);
    }

    public SimpleTridentItemEntity(TridentEntity trident) {
        this(trident.getWorld(), (LivingEntity) trident.getOwner(), ((TridentEntityAccessor) trident).getTridentStack());
        this.setVelocity(trident.getVelocity());
        this.setUuid(trident.getUuid());
        this.pickupType = trident.pickupType;
    }
}