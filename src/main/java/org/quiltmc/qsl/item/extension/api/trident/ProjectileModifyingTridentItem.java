package org.quiltmc.qsl.item.extension.api.trident;

import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;

public class ProjectileModifyingTridentItem extends TridentItem {
    public ProjectileModifyingTridentItem(Item.Properties settings) {
        super(settings);
    }

    /** Override to change throw velocity. Vanilla default is 2.5f. */
    public float getThrowPower(ItemStack stack) {
        return 2.5f;
    }

    /** Called after the ThrownTrident is created but before it is added to the world.
     *  Override to set damage, piercing, gravity, pickup behaviour, etc. */
    public void modifyThrownTrident(ItemStack stack, ThrownTrident trident) {}
}