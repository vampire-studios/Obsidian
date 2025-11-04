/*
package io.github.vampirestudios.obsidian.api.crucible.skills;

import io.lumine.mythic.api.skills.SkillTrigger;
import io.lumine.mythic.core.skills.triggers.meta.EntityAttackMetadata;

public class SkillTriggers {
  public static final SkillTrigger DEFAULT = SkillTrigger.create("DEFAULT");
  
  public static final SkillTrigger API = SkillTrigger.create("API");
  
  public static final SkillTrigger ATTACK = SkillTrigger.create("ATTACK", EntityAttackMetadata.class, new String[] { "HIT" });
  
  public static final SkillTrigger SKILL_HIT = SkillTrigger.create("SKILLHIT", "SKILL_DAMAGE", "SKILLDAMAGE");
  
  public static final SkillTrigger BOW_HIT = SkillTrigger.create("BOW_HIT", "BOWHIT");
  
  public static final SkillTrigger BLOCK = SkillTrigger.create("BLOCK");
  
  public static final SkillTrigger BLOCK_PLACE = SkillTrigger.create("BLOCK_PLACE", "BLOCKPLACE", "PLACEBLOCK", "PLACE_BLOCK");
  
  public static final SkillTrigger BLOCK_BREAK = SkillTrigger.create("BLOCK_BREAK", "BLOCKBREAK", "BREAKBLOCK", "BREAK_BLOCK");
  
  public static final SkillTrigger ITEM_PICKUP = SkillTrigger.create("ITEM_PICKUP", "ITEMPICKUP", "PICKUPITEM", "PICKUP_ITEM");
  
  public static final SkillTrigger ITEM_DROP = SkillTrigger.create("ITEM_DROP", "ITEMDROP", "DROPITEM", "DROP_ITEM");
  
  public static final SkillTrigger HEAR = SkillTrigger.create("HEAR", "VIBRATION");
  
  public static final SkillTrigger COMBAT = SkillTrigger.create("COMBAT");
  
  public static final SkillTrigger CONSUME = SkillTrigger.create("CONSUME", "EAT");
  
  public static final SkillTrigger CROUCH = SkillTrigger.create("CROUCH");
  
  public static final SkillTrigger CREEPER_CHARGE = SkillTrigger.create("CREEPER_CHARGE", "CREEPERCHARGE", "CHARGE", "CHARGED");
  
  public static final SkillTrigger UNCROUCH = SkillTrigger.create("UNCROUCH");
  
  public static final SkillTrigger JUMP = SkillTrigger.create("JUMP");
  
  public static final SkillTrigger DAMAGED = SkillTrigger.create("DAMAGED", "HURT");
  
  public static final SkillTrigger DROPCOMBAT = SkillTrigger.create("DROPCOMBAT", "LEAVECOMBAT", "COMBATDROP");
  
  public static final SkillTrigger DEATH = SkillTrigger.create("DEATH");
  
  public static final SkillTrigger DESPAWNED = SkillTrigger.create("DESPAWNED", "DESPAWN");
  
  public static final SkillTrigger ENTERCOMBAT = SkillTrigger.create("ENTERCOMBAT");
  
  public static final SkillTrigger EXPLODE = SkillTrigger.create("EXPLODE");
  
  public static final SkillTrigger PRIME = SkillTrigger.create("PRIME");
  
  public static final SkillTrigger INTERACT = SkillTrigger.create("INTERACT");
  
  public static final SkillTrigger KILL = SkillTrigger.create("KILL");
  
  public static final SkillTrigger KILLPLAYER = SkillTrigger.create("KILLPLAYER", "PLAYERKILL");
  
  public static final SkillTrigger PLAYERDEATH = SkillTrigger.create("PLAYERDEATH", "PLAYERDIE");
  
  public static final SkillTrigger SHOOT = SkillTrigger.create("SHOOT", "BOWSHOOT", "SHOOTBOW");
  
  public static final SkillTrigger SIGNAL = SkillTrigger.create("SIGNAL");
  
  public static final SkillTrigger SPAWN = SkillTrigger.create("SPAWN");
  
  public static final SkillTrigger SPAWN_OR_LOAD = SkillTrigger.create("SPAWNORLOAD");
  
  public static final SkillTrigger SPLASH_POTION = SkillTrigger.create("SPLASH_POTION", "POTIONSPLASH", "SPLASHPOTION");
  
  public static final SkillTrigger SWING = SkillTrigger.create("SWING", "LEFTCLICK");
  
  public static final SkillTrigger TARGETCHANGE = SkillTrigger.create("TARGETCHANGE", "CHANGETARGET");
  
  public static final SkillTrigger TARGETED = SkillTrigger.create("TARGETED");
  
  public static final SkillTrigger TELEPORT = SkillTrigger.create("TELEPORT");
  
  public static final SkillTrigger TIMER = SkillTrigger.create("TIMER");
  
  public static final SkillTrigger USE = SkillTrigger.create("USE");
  
  public static final SkillTrigger RIGHTCLICK = SkillTrigger.create("RIGHTCLICK");
  
  public static final SkillTrigger READY = SkillTrigger.create("READY", "FIRSTSPAWN");
  
  public static final SkillTrigger CAST = SkillTrigger.create("CAST");
  
  public static final SkillTrigger FISH = SkillTrigger.create("FISH", "FISHING", "FISHINGCAST");
  
  public static final SkillTrigger FISH_BITE = SkillTrigger.create("FISH_BITE", "FISHBITE", "FISHING_BITE", "FISHINGBITE");
  
  public static final SkillTrigger FISH_CATCH_FISH = SkillTrigger.create("FISHCATCH", "FISHCAUGHT", "FISHINGCATCH", "FISHINGCAUGHT", "CATCHFISH", "CAUGHTFISH");
  
  public static final SkillTrigger FISH_CATCH_ENTITY = SkillTrigger.create("FISHGRAB", "FISHINGGRAB", "FISHENTITY", "FISHINGENTITY");
  
  public static final SkillTrigger FISH_GROUND = SkillTrigger.create("FISH_GROUND", "FISHINGGROUND", "FISHGROUND");
  
  public static final SkillTrigger FISH_REEL = SkillTrigger.create("FISH_REEL", "FISHINGREEL", "FISHREEL");
  
  public static final SkillTrigger FISH_FAIL = SkillTrigger.create("FISH_FAIL", "FISHFAIL", "FISHINGFAIL");
  
  public static final SkillTrigger TAME = SkillTrigger.create("TAME");
  
  public static final SkillTrigger TAME_FAIL = SkillTrigger.create("TAME_FAIL", "TAMEFAIL");
  
  public static final SkillTrigger TRIDENT_THROW = SkillTrigger.create("TRIDENT_THROW", "THROWTRIDENT", "TRIDENTTHROW");
  
  public static final SkillTrigger TRIDENT_HIT = SkillTrigger.create("TRIDENT_HIT");
  
  public static final SkillTrigger ARMOR_EQUIP = SkillTrigger.create("EQUIP", "EQUIPARMOR", "ARMOREQUIP");
  
  public static final SkillTrigger ARMOR_UNEQUIP = SkillTrigger.create("UNEQUIP", "UNEQUIPARMOR", "ARMORUNEQUIP");
  
  public static final SkillTrigger PRESS_Q = SkillTrigger.create("PRESS_Q", "PRESSQ", "DROPITEM");
  
  public static final SkillTrigger PRESS_CTRLQ = SkillTrigger.create("PRESS_CTRL_Q", "PRESSCTRLQ", "PRESS_CTRLQ", "DROPITEMSTACK");
  
  public static final SkillTrigger PRESS_F = SkillTrigger.create("PRESS_F", "ITEMSWAP", "SWAPITEMS", "PRESSF");
  
  public static final SkillTrigger MOUNT = SkillTrigger.create("MOUNT");
  
  public static final SkillTrigger UNMOUNT = SkillTrigger.create("UNMOUNT");
  
  public static final SkillTrigger LOAD = SkillTrigger.create("LOAD");
  
  public static final SkillTrigger CUSTOM = SkillTrigger.create("CUSTOM");
  
  public static final SkillTrigger PRESS = SkillTrigger.create("PRESS");
  
  public static final SkillTrigger RELEASE = SkillTrigger.create("RELEASE");
  
  public static final SkillTrigger JOIN = SkillTrigger.create("JOIN");
  
  public static final SkillTrigger RESPAWN = SkillTrigger.create("RESPAWN");
  
  public static final SkillTrigger BREED = SkillTrigger.create("BREED");
  
  public static final SkillTrigger TRADE = SkillTrigger.create("TRADE");
  
  public static final SkillTrigger CHANGE_WORLD = SkillTrigger.create("CHANGE_WORLD", "CHANGEWORLD", "WORLD_CHANGE", "WORLDCHANGE");
  
  public static final SkillTrigger BUCKET = SkillTrigger.create("BUCKET", "USEBUCKET", "FILLBUCKET", "BUCKETFILL", "MILK", "MILKED");
  
  static {
    DEFAULT.register();
    API.register();
    ARMOR_EQUIP.register();
    ARMOR_UNEQUIP.register();
    ATTACK.register();
    SKILL_HIT.register();
    BLOCK.register();
    BLOCK_BREAK.register();
    BLOCK_PLACE.register();
    ITEM_PICKUP.register();
    ITEM_DROP.register();
    BOW_HIT.register();
    CAST.register();
    COMBAT.register();
    CONSUME.register();
    CREEPER_CHARGE.register();
    CROUCH.register();
    CUSTOM.register();
    DAMAGED.register();
    DEATH.register();
    DESPAWNED.register();
    DROPCOMBAT.register();
    ENTERCOMBAT.register();
    EXPLODE.register();
    FISH.register();
    FISH_BITE.register();
    FISH_CATCH_ENTITY.register();
    FISH_CATCH_FISH.register();
    FISH_FAIL.register();
    FISH_GROUND.register();
    FISH_REEL.register();
    INTERACT.register();
    HEAR.register();
    JUMP.register();
    KILL.register();
    KILLPLAYER.register();
    LOAD.register();
    MOUNT.register();
    PLAYERDEATH.register();
    PRESS_CTRLQ.register();
    PRESS_F.register();
    PRESS_Q.register();
    PRIME.register();
    READY.register();
    RIGHTCLICK.register();
    SHOOT.register();
    SIGNAL.register();
    SPAWN.register();
    SPAWN_OR_LOAD.register();
    SPLASH_POTION.register();
    SWING.register();
    TAME.register();
    TAME_FAIL.register();
    TARGETCHANGE.register();
    TARGETED.register();
    TELEPORT.register();
    TIMER.register();
    TRIDENT_HIT.register();
    TRIDENT_THROW.register();
    UNCROUCH.register();
    UNMOUNT.register();
    USE.register();
    RELEASE.register();
    PRESS.register();
    JOIN.register();
    RESPAWN.register();
    BREED.register();
    TRADE.register();
    CHANGE_WORLD.register();
    BUCKET.register();
  }
}*/
