package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.RangedWeaponItem;
import io.github.vampirestudios.obsidian.minecraft.TridentComponent;
import io.github.vampirestudios.obsidian.registry.OItemComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import org.quiltmc.qsl.item.extension.api.trident.ProjectileModifyingTridentItem;
import org.quiltmc.qsl.item.extension.api.trident.TridentExtensions;

public class TridentItemImpl extends ProjectileModifyingTridentItem implements TridentExtensions {
    public final RangedWeaponItem rangedWeaponItem;

    public TridentItemImpl(RangedWeaponItem rangedWeaponItem, Properties settings) {
        super(settings);
        this.rangedWeaponItem = rangedWeaponItem;
    }

    @Override
    public float getThrowPower(ItemStack stack) {
        TridentComponent comp = stack.get(OItemComponents.TRIDENT);
        return comp != null ? comp.throwPower() : 2.5f;
    }

    @Override
    public void modifyThrownTrident(ItemStack stack, ThrownTrident trident) {
        TridentComponent comp = stack.get(OItemComponents.TRIDENT);
        if (comp == null) return;

        trident.setBaseDamage(comp.damage());

        if (comp.piercing() > 0) {
//            trident.setPierceLevel((byte) comp.piercing());
        }

        if (comp.noGravity()) {
            trident.setNoGravity(true);
        }

        if (comp.disappearsOnHit()) {
            trident.pickup = AbstractArrow.Pickup.DISALLOWED;
        }

        comp.throwSound().ifPresent(soundId ->
                BuiltInRegistries.SOUND_EVENT.getOptional(soundId).ifPresent(sound ->
                        trident.level().playSound(null, trident, sound, SoundSource.PLAYERS, 1.0f, 1.0f)
                )
        );
    }

    @Override
    public boolean useVanillaRenderer() {
        return rangedWeaponItem.information == null/*
                || rangedWeaponItem.information.getDisplayInformation() == null*/;
    }

    @Override
    public Identifier getRenderTexture() {
        if (rangedWeaponItem.information == null
                || rangedWeaponItem.information.id == null) {
            return null;
        }
        Identifier id = rangedWeaponItem.information.id;
        return Identifier.fromNamespaceAndPath(id.getNamespace(), "textures/item/" + id.getPath() + ".png");
    }
}
