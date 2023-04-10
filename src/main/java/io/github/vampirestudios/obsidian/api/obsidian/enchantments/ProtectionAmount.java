package io.github.vampirestudios.obsidian.api.obsidian.enchantments;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;

public class ProtectionAmount {

    public int level = 1;
    public String damage_source = "generic";
    public int protection_amount = 1;

    public DamageSource getDamageSource(Entity source) {
        DamageSources damageSources = source.getDamageSources();
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
            case "anvil" -> damageSources.fallingAnvil(source);
            case "fallingBlock" -> damageSources.fallingBlock(source);
            case "dragonBreath" -> damageSources.dragonBreath();
            case "dryout" -> damageSources.dryOut();
            case "sweetBerryBush" -> damageSources.sweetBerryBush();
            default -> damageSources.generic();
        };
    }

}
