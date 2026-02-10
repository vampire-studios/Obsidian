package io.github.vampirestudios.obsidian.api.crucible;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum SkillTrigger {
    // -------------------------
    // Core / Mythic parity
    // -------------------------
    DEFAULT("DEFAULT"),
    API("API"),

    ATTACK("ATTACK", "HIT"),
    SKILL_HIT("SKILLHIT", "SKILL_DAMAGE", "SKILLDAMAGE"),
    BOW_HIT("BOW_HIT", "BOWHIT", "ARROWHIT"),

    BLOCK("BLOCK"),
    BLOCK_PLACE("BLOCK_PLACE", "BLOCKPLACE", "PLACEBLOCK", "PLACE_BLOCK"),
    BLOCK_BREAK("BLOCK_BREAK", "BLOCKBREAK", "BREAKBLOCK", "BREAK_BLOCK"),

    ITEM_PICKUP("ITEM_PICKUP", "ITEMPICKUP", "PICKUPITEM", "PICKUP_ITEM"),
    ITEM_DROP("ITEM_DROP", "ITEMDROP", "DROPITEM", "DROP_ITEM"),

    HEAR("HEAR", "VIBRATION"),

    COMBAT("COMBAT"),
    CONSUME("CONSUME", "EAT"),

    CROUCH("CROUCH"),
    UNCROUCH("UNCROUCH"),

    CREEPER_CHARGE("CREEPER_CHARGE", "CREEPERCHARGE", "CHARGE", "CHARGED"),

    JUMP("JUMP"),
    DAMAGED("DAMAGED", "HURT"),

    DROPCOMBAT("DROPCOMBAT", "LEAVECOMBAT", "COMBATDROP", "EXITCOMBAT"),
    ENTERCOMBAT("ENTERCOMBAT"),

    DEATH("DEATH"),
    DESPAWNED("DESPAWNED", "DESPAWN"),

    EXPLODE("EXPLODE"),
    PRIME("PRIME"),

    CREEPER_EXPLODE("CREEPER_EXPLODE"),
    CREEPER_PRIME("CREEPER_PRIME"),

    INTERACT("INTERACT"),

    KILL("KILL"),
    KILLPLAYER("KILLPLAYER", "PLAYERKILL"),
    PLAYERDEATH("PLAYERDEATH", "PLAYERDIE"),

    SHOOT("SHOOT", "BOWSHOOT", "SHOOTBOW"),
    SIGNAL("SIGNAL"),

    SPAWN("SPAWN"),
    SPAWN_OR_LOAD("SPAWNORLOAD"),

    SPLASH_POTION("SPLASH_POTION", "POTIONSPLASH", "SPLASHPOTION"),

    SWING("SWING", "LEFTCLICK"),
    TARGETCHANGE("TARGETCHANGE", "CHANGETARGET"),
    TARGETED("TARGETED"),

    TELEPORT("TELEPORT"),
    TIMER("TIMER"),

    USE("USE"),
    RIGHTCLICK("RIGHTCLICK"),
    READY("READY", "FIRSTSPAWN"),
    CAST("CAST"),

    FISH("FISH", "FISHING", "FISHINGCAST"),
    FISH_BITE("FISH_BITE", "FISHBITE", "FISHING_BITE", "FISHINGBITE"),
    FISH_CATCH_FISH("FISHCATCH", "FISHCAUGHT", "FISHINGCATCH", "FISHINGCAUGHT", "CATCHFISH", "CAUGHTFISH"),
    FISH_CATCH_ENTITY("FISHGRAB", "FISHINGGRAB", "FISHENTITY", "FISHINGENTITY"),
    FISH_GROUND("FISH_GROUND", "FISHINGGROUND", "FISHGROUND"),
    FISH_REEL("FISH_REEL", "FISHINGREEL", "FISHREEL"),
    FISH_FAIL("FISH_FAIL", "FISHFAIL", "FISHINGFAIL"),

    TAME("TAME"),
    TAME_FAIL("TAME_FAIL", "TAMEFAIL"),

    ARMOR_EQUIP("EQUIP", "EQUIPARMOR", "ARMOREQUIP"),
    ARMOR_UNEQUIP("UNEQUIP", "UNEQUIPARMOR", "ARMORUNEQUIP"),

    PRESS_Q("PRESS_Q", "PRESSQ"),
    PRESS_CTRLQ("PRESS_CTRL_Q", "PRESSCTRLQ", "PRESS_CTRLQ", "DROPITEMSTACK"),
    PRESS_F("PRESS_F", "ITEMSWAP", "SWAPITEMS", "PRESSF"),

    PROJECTILE_HIT("PROJECTILE_HIT", "PROJECTILEHIT", "TRIDENT_HIT", "TRIDENTHIT"),
    PROJECTILE_LAND("PROJECTILE_LAND", "PROJECTILELAND", "TRIDENTLAND"),
    PROJECTILE_THROW("PROJECTILE_THROW", "PROJECTILETHROW", "THROW", "TRIDENT_THROW", "THROWTRIDENT", "TRIDENTTHROW"),

    MOUNT("MOUNT"),
    DISMOUNT("UNMOUNT", "DISMOUNT"),
    DISMOUNTED("UNMOUNTED", "DISMOUNTED"),

//    LOAD("LOAD"),
//
//    CUSTOM("CUSTOM"),

//    PRESS("PRESS"),
//    RELEASE("RELEASE"),

    TICK("TICK"),

    JOIN("JOIN"),
    RESPAWN("RESPAWN"),

    BREED("BREED"),
    TRADE("TRADE"),

    CHANGE_WORLD("CHANGE_WORLD", "CHANGEWORLD", "WORLD_CHANGE", "WORLDCHANGE"),

    BUCKET("BUCKET", "USEBUCKET", "FILLBUCKET", "BUCKETFILL", "MILK", "MILKED"),

    // -------------------------
    // Mod-only extras (optional)
    // -------------------------
    CHAT("CHAT"),
    COMMAND("COMMAND"),
    DIMENSION_SLEEP("SLEEP"),
    ITEM_CRAFT("CRAFT"),
    ITEM_SMELT("SMELT"),
    ADVANCEMENT("ADVANCEMENT"),
    BLOCK_USE("BLOCK_USE"),
    ENTITY_INTERACT("ENTITY_INTERACT"),
    INVENTORY_OPEN("INVENTORY_OPEN"),
    INVENTORY_CLOSE("INVENTORY_CLOSE"),
    DAMAGE_DEALT("DAMAGE_DEALT");

    private final String canonical;
    private final String[] aliases;

    SkillTrigger(String canonical, String... aliases) {
        this.canonical = canonical;
        this.aliases = aliases;
    }

    public String canonical() {
        return canonical;
    }

    // Backwards-compatible with your old API shape
    public String getName() {
        return STR."on\{canonical.toLowerCase(Locale.ROOT)}";
    }

    // -------------------------
    // Lookup (Mythic-like)
    // -------------------------
    private static final Map<String, SkillTrigger> LOOKUP = new HashMap<>();

    static {
        for (SkillTrigger t : values()) {
            // register canonical + enum name + "onX"
            reg(t, t.canonical);
            reg(t, t.name());
            reg(t, STR."on\{t.canonical}");

            // register aliases
            for (String a : t.aliases) reg(t, a);
        }
    }

    private static void reg(SkillTrigger t, String name) {
        LOOKUP.put(normalize(name), t);
    }

    private static String normalize(String s) {
        if (s == null) return "";
        String x = s.trim().toUpperCase(Locale.ROOT);

        // allow "~onUse" style inputs
        if (x.startsWith("ON")) x = x.substring(2);

        // strip separators
        x = x.replace("_", "")
                .replace("-", "")
                .replace(" ", "");

        return x;
    }

    public static SkillTrigger findByName(String name) {
        SkillTrigger t = LOOKUP.get(normalize(name));
        return t != null ? t : DEFAULT;
    }
}
