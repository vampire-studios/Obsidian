package io.github.vampirestudios.obsidian.api.obsidian.enchantments;

import net.minecraft.entity.damage.DamageSource;

public class ProtectionAmount {

    public int level = 1;
    public String damage_source = "generic";
    public int protection_amount = 1;

    public DamageSource getDamageSource() {
        switch(damage_source) {
            case "inFire":
                return DamageSource.IN_FIRE;
            case "lightningBolt":
                return DamageSource.LIGHTNING_BOLT;
            case "onFire":
                return DamageSource.ON_FIRE;
            case "lava":
                return DamageSource.LAVA;
            case "hotFloor":
                return DamageSource.HOT_FLOOR;
            case "inWall":
                return DamageSource.IN_WALL;
            case "cramming":
                return DamageSource.CRAMMING;
            case "drown":
                return DamageSource.DROWN;
            case "starve":
                return DamageSource.STARVE;
            case "cactus":
                return DamageSource.CACTUS;
            case "fall":
                return DamageSource.FALL;
            case "flyIntoWall":
                return DamageSource.FLY_INTO_WALL;
            case "outOfWorld":
                return DamageSource.OUT_OF_WORLD;
            case "magic":
                return DamageSource.MAGIC;
            case "wither":
                return DamageSource.WITHER;
            case "anvil":
                return DamageSource.ANVIL;
            case "fallingBlock":
                return DamageSource.FALLING_BLOCK;
            case "dragonBreath":
                return DamageSource.DRAGON_BREATH;
            case "dryout":
                return DamageSource.DRYOUT;
            case "sweetBerryBush":
                return DamageSource.SWEET_BERRY_BUSH;
            case "generic":
            default:
                return DamageSource.GENERIC;
        }
    }

}
