package io.github.vampirestudios.obsidian.api.crucible;

public enum SkillTrigger {
    ON_USE("onUse"),
    ON_SWING("onSwing"),
    ON_TICK("onTick"),
    ON_CROUCH("onCrouch"),
    ON_TIMER("onTimer"),
    ON_BLOCK_PLACE("onBlockPlace"),
    ON_BLOCK_BREAK("onBlockBreak"),
    ON_BLOCK_ROTATE("onBlockRotate"),
    ON_ITEM_PICKUP("onItemPickup"),
    ON_ITEM_DROP("onItemDrop"),
    ON_DAMAGED("onDamaged"),
    ON_INTERACT("onInteract"),
    ON_HEAR("onHear"),
    ON_CONSUME("onConsume"),
    ON_UNCROUCH("onUnCrouch"),
    ON_JUMP("onJump"),
    ON_ENTER_COMBAT("onEnterCombat"),
    ON_DROP_COMBAT("onDropCombat"),
    ON_DESPAWN("onDespawn"),
    ON_EXPLODE("onExplode"),
    ON_PRIME("onPrime"),
    ON_KILL("onKill"),
    ON_KILLPLAYER("onPlayerKill"),
    ON_PLAYER_DEATH("onPlayerDeath"),
    ON_SHOOT("onShoot"),
    ON_SIGNAL("onSignal"),
    ON_SPAWN("onSpawn"),
    ON_TELEPORT("onTeleport"),
    ON_RIGHT_CLICK("onRightClick"),
    ON_LEFT_CLICK("onLeftClick"),
    ON_PRESS_Q("onPressQ"),
    ON_PRESS_CTRL_Q("onPressCtrlQ"),
    ON_PRESS_F("onPressF"),
    ON_COMBAT("onCombat");

    private String name;

    SkillTrigger(String name) {
        this.name = name;
    }

    public static SkillTrigger findByName(String name) {
        for (SkillTrigger trigger : values()) {
            if (trigger.name.equalsIgnoreCase(name)) {
                return trigger;
            }
        }
        return null; // or throw an exception if you prefer
    }

    public String getName() {
        return name;
    }
}
