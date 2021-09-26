package io.github.vampirestudios.obsidian.api.obsidian.enchantments;

import net.minecraft.entity.damage.DamageSource;

public class ProtectionAmount {

    public int level = 1;
    public String damage_source = "generic";
    public int protection_amount = 1;

    public DamageSource getDamageSource() {
        return switch (damage_source) {
            case "inFire" -> DamageSource.IN_FIRE;
            case "lightningBolt" -> DamageSource.LIGHTNING_BOLT;
            case "onFire" -> DamageSource.ON_FIRE;
            case "lava" -> DamageSource.LAVA;
            case "hotFloor" -> DamageSource.HOT_FLOOR;
            case "inWall" -> DamageSource.IN_WALL;
            case "cramming" -> DamageSource.CRAMMING;
            case "drown" -> DamageSource.DROWN;
            case "starve" -> DamageSource.STARVE;
            case "cactus" -> DamageSource.CACTUS;
            case "fall" -> DamageSource.FALL;
            case "flyIntoWall" -> DamageSource.FLY_INTO_WALL;
            case "outOfWorld" -> DamageSource.OUT_OF_WORLD;
            case "magic" -> DamageSource.MAGIC;
            case "wither" -> DamageSource.WITHER;
            case "anvil" -> DamageSource.ANVIL;
            case "fallingBlock" -> DamageSource.FALLING_BLOCK;
            case "dragonBreath" -> DamageSource.DRAGON_BREATH;
            case "dryout" -> DamageSource.DRYOUT;
            case "sweetBerryBush" -> DamageSource.SWEET_BERRY_BUSH;
            default -> DamageSource.GENERIC;
        };
    }

}
