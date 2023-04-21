package io.github.vampirestudios.obsidian.api.obsidian.enchantments;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;

public class ProtectionAmount {

    public int level = 1;
    public String damage_source = "generic";
    public int protection_amount = 1;

    public DamageSource getDamageSource(Entity source) {
        DamageSources damageSources = source.damageSources();
        return switch (damage_source) {
            case "inFire" -> damageSources.inFire();
            case "lightningBolt" -> damageSources.lightningBolt();
            case "onFire" -> damageSources.onFire();
            case "lava" -> damageSources.lava();
            case "hotFloor" -> damageSources.hotFloor();
            case "inWall" -> damageSources.inWall();
            case "cramming" -> damageSources.cramming();
            case "drown" -> damageSources.drown();
            case "starve" -> damageSources.starve();
            case "cactus" -> damageSources.cactus();
            case "fall" -> damageSources.fall();
            case "flyIntoWall" -> damageSources.flyIntoWall();
            case "outOfWorld" -> damageSources.outOfWorld();
            case "magic" -> damageSources.magic();
            case "wither" -> damageSources.wither();
            case "anvil" -> damageSources.anvil(source);
            case "fallingBlock" -> damageSources.fallingBlock(source);
            case "dragonBreath" -> damageSources.dragonBreath();
            case "dryout" -> damageSources.dryOut();
            case "sweetBerryBush" -> damageSources.sweetBerryBush();
            default -> damageSources.generic();
        };
    }

}
