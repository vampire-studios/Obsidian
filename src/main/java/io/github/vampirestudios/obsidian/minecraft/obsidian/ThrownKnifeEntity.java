package io.github.vampirestudios.obsidian.minecraft.obsidian;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class ThrownKnifeEntity extends ThrowableItemProjectile {

    private float damage = 3.0f;

    public ThrownKnifeEntity(EntityType<? extends ThrownKnifeEntity> type, Level level) {
        super(type, level);
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Override
    protected Item getDefaultItem() {
        return Items.STONE_SWORD;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        result.getEntity().hurtServer(
				(ServerLevel) result.getEntity().level(),
                this.damageSources().thrown(this, this.getOwner()),
                this.damage
        );
    }
}
