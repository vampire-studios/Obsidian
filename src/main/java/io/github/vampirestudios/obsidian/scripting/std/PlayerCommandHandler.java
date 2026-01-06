package io.github.vampirestudios.obsidian.scripting.std;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import io.github.vampirestudios.obsidian.client.GuiBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayerCommandHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger("PlayerCommandHandler");

    @Override
    public void handleCall(MinecraftServer server, List<CallChain.Segment> segments, Map<String, Object> vars) {
        if (segments.isEmpty()) {
            LOGGER.warn("No method specified for player call");
            return;
        }
        Object o = vars.get("sender");
        if (!(o instanceof ServerPlayer player)) {
            LOGGER.warn("No valid player found in variables");
            return;
        }
        CallChain.Segment segment = segments.getFirst();

        // NEW: nested player.gui.*
        if ("gui".equals(segment.name())) {
            handlePlayerGui(player, segments.subList(1, segments.size()), vars);
            return;
        }

        try {
            switch (segment.name()) {
                case "msg" -> sendMessage(player, ScriptUtils.getStringArg(segment, 0, vars));
                case "actionbar" -> sendActionBar(player, ScriptUtils.getStringArg(segment, 0, vars));
                case "title" -> sendTitle(player, segment, vars);
                case "give" -> giveItem(player, segment, vars);
                case "take" -> takeItem(player, segment, vars);
                case "effect" -> applyEffect(player, segment, vars);
                case "removeEffect" -> {
                    String id = ScriptUtils.getStringArg(segment, 0, vars);
                    var opt = BuiltInRegistries.MOB_EFFECT.get(Identifier.parse(id));
                    boolean removed = opt.map(player::removeEffect).orElse(false);
                    vars.put("_last", removed);
                }
                case "clearEffects" -> clearEffects(player);
                case "sound" -> playSound(player, segment, vars);
                case "teleport" -> teleport(player, segment, vars);
                case "heal" -> player.heal(Math.max(0f, (float) ScriptUtils.getNumberArg(segment, 0, vars)));
                case "xp" -> player.giveExperiencePoints((int) ScriptUtils.getNumberArg(segment, 0, vars));
                case "level" -> player.giveExperienceLevels((int) ScriptUtils.getNumberArg(segment, 0, vars));
                case "gamemode" -> setGameMode(player, ScriptUtils.getStringArg(segment, 0, vars));
                case "inventory" -> handleInventory(player, segments.subList(1, segments.size()), vars);
                case "name" -> vars.put("_last", player.getGameProfile().name());
                case "setHealth" -> setHealth(player, segment, vars);
                case "addTag" -> addTag(player, ScriptUtils.getStringArg(segment, 0, vars));
                case "removeTag" -> removeTag(player, ScriptUtils.getStringArg(segment, 0, vars));
                case "hasTag" -> {
                    String tag = ScriptUtils.getStringArg(segment, 0, vars);
                    vars.put("_last", player.getTags().contains(tag));
                }
                case "tellRaw" -> {
                    String json = ScriptUtils.getStringArg(segment, 0, vars);
                    try {
                        var comp = parseComponentNew(json);
                        player.sendSystemMessage(comp);
                    } catch (Exception e) {
                        player.sendSystemMessage(Component.literal("[obs] bad json: " + e.getMessage()));
                    }
                }
                case "attribute" -> {
                    String attr = ScriptUtils.getStringArg(segment, 0, vars);     // e.g. "minecraft:generic.max_health"
                    String op   = ScriptUtils.getStringArg(segment, 1, vars);     // "setBase"|"addMod"|"removeMod"
                    double val  = ScriptUtils.getNumberArg(segment, 2, vars);

                    var holder = BuiltInRegistries.ATTRIBUTE.get(Identifier.parse(attr))
                            .orElseThrow(() -> new IllegalArgumentException("Unknown attribute " + attr));
                    var inst = player.getAttribute(holder);
                    if (inst == null) throw new IllegalArgumentException("No instance for " + attr);

                    switch (op) {
                        case "setBase" -> inst.setBaseValue(val);
                        case "addMod"  -> inst.addPermanentModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                                Identifier.parse(attr), val,
                                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE));
                        case "removeMod" -> inst.removeModifier(Identifier.parse(attr));
                        default -> throw new IllegalArgumentException("Bad op " + op);
                    }
                }
                default -> LOGGER.warn("Unknown player method: {}", segment.name());
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error("Error executing player method {}: {}", segment.name(), e.getMessage());
        }
    }

    // NEW: nested player.gui.* handler
    private void handlePlayerGui(ServerPlayer player, List<CallChain.Segment> sub, Map<String, Object> vars) {
        if (sub.isEmpty()) {
            LOGGER.warn("player.gui requires a sub-method");
            return;
        }
        CallChain.Segment s2 = sub.getFirst();
        switch (s2.name()) {
            case "create" -> {
                int rows = (int) ScriptUtils.getNumberArg(s2, 0, vars);
                String title = s2.args().size() >= 2 ? ScriptUtils.getStringArg(s2, 1, vars) : "";
                String id = GuiBridge.create(player, rows, Component.literal(title));
                vars.put("_last_gui", id); vars.put("_last", id);
            }
            case "open" -> {
                String id = ScriptUtils.getStringArg(s2, 0, vars);
                GuiBridge.open(id, player);
                vars.put("_last", true);
            }
            case "close" -> {
                String id = ScriptUtils.getStringArg(s2, 0, vars);
                GuiBridge.close(id, player);
                vars.put("_last", true);
            }
            default -> LOGGER.warn("Unknown player.gui method: {}", s2.name());
        }
    }

    public static Component parseComponentNew(String json) {
        return ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(json)).getOrThrow();
    }

    @Override
    public void handleCallWithBlock(MinecraftServer server, List<CallChain.Segment> segments, Stmt.Block body, Map<String, Object> vars) {
        LOGGER.warn("Player method with block not supported for segments: {}", segments);
    }

    private void sendMessage(ServerPlayer player, String message) {
        player.sendSystemMessage(Component.literal(message));
    }

    private void sendActionBar(ServerPlayer player, String message) {
        player.displayClientMessage(Component.literal(message), true);
    }

    private void sendTitle(ServerPlayer player, CallChain.Segment segment, Map<String, Object> vars) {
        String title = ScriptUtils.getStringArg(segment, 0, vars);
        String subtitle = segment.args().size() >= 2 ? ScriptUtils.getStringArg(segment, 1, vars) : "";
        int fadeIn = segment.args().size() >= 3 ? (int) ScriptUtils.getNumberArg(segment, 2, vars) : 10;
        int stay = segment.args().size() >= 4 ? (int) ScriptUtils.getNumberArg(segment, 3, vars) : 60;
        int fadeOut = segment.args().size() >= 5 ? (int) ScriptUtils.getNumberArg(segment, 4, vars) : 10;
        if (!title.isEmpty()) {
            player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket(Component.literal(title)));
        }
        if (!subtitle.isEmpty()) {
            player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket(Component.literal(subtitle)));
        }
        player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket(fadeIn, stay, fadeOut));
    }

    private void giveItem(ServerPlayer player, CallChain.Segment segment, Map<String, Object> vars) {
        if (segment.args().size() < 2) {
            LOGGER.warn("give requires item ID and count");
            return;
        }
        String id = ScriptUtils.getStringArg(segment, 0, vars);
        int count = segment.args().size() >= 2 ? (int) ScriptUtils.getNumberArg(segment, 1, vars) : 1;

        Item item = ScriptUtils.getItem(ScriptUtils.getStringArg(segment, 0, vars));

        ItemStack stack = new ItemStack(ScriptUtils.getItem(id), ScriptUtils.clamp(count, 1, 64));

        // Third arg = component map
        if (segment.args().size() >= 3) {
            Object arg3 = ScriptUtils.eval(segment.args().get(2), vars); // however your DSL exposes object literals
            if (arg3 instanceof Map<?,?> map) {
                applyComponents(stack, map);
            }
        }

        player.getInventory().placeItemBackInInventory(stack);
    }

    @SuppressWarnings("unchecked")
    public static void applyComponents(ItemStack stack, Map<?,?> map) {
        for (Map.Entry<?,?> e : map.entrySet()) {
            String key = e.getKey().toString();                 // e.g. "minecraft:item_name"
            Object val = e.getValue();
            var holder = BuiltInRegistries.DATA_COMPONENT_TYPE.get(Identifier.parse(key))
                    .orElseThrow(() -> new IllegalArgumentException("Unknown component: " + key));
            var type = holder.value();
            JsonElement json = ScriptUtils.toJsonElement(val);
            Object parsed = type.codec().parse(JsonOps.INSTANCE, json).getOrThrow();
            stack.set((DataComponentType<Object>) type, parsed);
        }
    }

    private void takeItem(ServerPlayer player, CallChain.Segment segment, Map<String, Object> vars) {
        if (segment.args().size() < 2) {
            LOGGER.warn("take requires item ID and count");
            return;
        }
        String id = ScriptUtils.getStringArg(segment, 0, vars);
        int toRemove = (int) ScriptUtils.getNumberArg(segment, 1, vars);
        for (int i = 0; i < player.getInventory().getContainerSize() && toRemove > 0; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(id)) {
                int removeCount = Math.min(toRemove, stack.getCount());
                stack.shrink(removeCount);
                toRemove -= removeCount;
            }
        }
    }

    private void applyEffect(ServerPlayer player, CallChain.Segment segment, Map<String, Object> vars) {
        if (segment.args().size() < 2) {
            LOGGER.warn("effect requires effect ID and duration");
            return;
        }
        if ("clear".equals(ScriptUtils.getStringArg(segment, 0, vars))) {
            clearEffects(player);
            return;
        }
        Holder<MobEffect> effect = BuiltInRegistries.MOB_EFFECT
                .get(Identifier.parse(ScriptUtils.getStringArg(segment, 0, vars)))
                .orElseThrow(() ->
                        new IllegalArgumentException(STR."Unknown effect: \{ScriptUtils.getStringArg(segment, 0, vars)}")
                );
        int seconds = (int) ScriptUtils.getNumberArg(segment, 1, vars);
        int amplifier = segment.args().size() >= 3 ? (int) ScriptUtils.getNumberArg(segment, 2, vars) : 0;
        boolean ambient = segment.args().size() >= 4 && ScriptUtils.getBooleanArg(segment, 3, vars);
        boolean showParticles = segment.args().size() < 5 || ScriptUtils.getBooleanArg(segment, 4, vars);

        int ticks = Math.max(1, Math.min(20 * Math.max(0, seconds), 20 * 60 * 60 * 24)); // Cap at 24 hours
        player.addEffect(new MobEffectInstance(effect, ticks, Math.max(0, amplifier), ambient, showParticles));
    }

    private void playSound(ServerPlayer player, CallChain.Segment segment, Map<String, Object> vars) {
        if (segment.args().size() < 3) {
            LOGGER.warn("sound requires sound ID, volume, and pitch");
            return;
        }
        SoundEvent sound = BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse(ScriptUtils.getStringArg(segment, 0, vars)));
        if (sound == null) {
            LOGGER.warn("Unknown sound: {}", ScriptUtils.getStringArg(segment, 0, vars));
            return;
        }
        float volume = (float) ScriptUtils.getNumberArg(segment, 1, vars);
        float pitch = (float) ScriptUtils.getNumberArg(segment, 2, vars);
        int x = segment.args().size() >= 6 ? Mth.floor(ScriptUtils.getNumberArg(segment, 3, vars)) : player.getBlockX();
        int y = segment.args().size() >= 6 ? Mth.floor(ScriptUtils.getNumberArg(segment, 4, vars)) : player.getBlockY();
        int z = segment.args().size() >= 6 ? Mth.floor(ScriptUtils.getNumberArg(segment, 5, vars)) : player.getBlockZ();
        SoundSource src = segment.args().size() >= 7 ? SoundSource.valueOf(ScriptUtils.getStringArg(segment, 6, vars).toUpperCase()) : SoundSource.PLAYERS;
        player.level().playSound(player, new BlockPos(x, y, z), sound, src, volume, pitch);
    }

    private void teleport(ServerPlayer player, CallChain.Segment segment, Map<String, Object> vars) {
        int idx = 0;
        var level = player.level();
        String first = ScriptUtils.getStringArg(segment, 0, vars);

        if (first.contains(":")) { // looks like a dimension id
            level = player.level().getServer().getLevel(ResourceKey.create(Registries.DIMENSION, Identifier.parse(first)));
            if (level == null) { LOGGER.warn("Unknown dimension: {}", first); return; }
            idx = 1;
        }
        if (segment.args().size() - idx < 3) { LOGGER.warn("teleport requires x y z"); return; }

        double x = ScriptUtils.getNumberArg(segment, 0, vars);
        double y = ScriptUtils.getNumberArg(segment, 1, vars);
        double z = ScriptUtils.getNumberArg(segment, 2, vars);
        float yaw = segment.args().size() >= 4 ? (float) ScriptUtils.getNumberArg(segment, 3, vars) : player.getYRot();
        float pitch = segment.args().size() >= 5 ? (float) ScriptUtils.getNumberArg(segment, 4, vars) : player.getXRot();
        player.teleportTo(level, x, y, z, Set.of(), yaw, pitch, false);
    }

    private void setGameMode(ServerPlayer player, String mode) {
        GameType gameType = switch (mode) {
            case "creative" -> GameType.CREATIVE;
            case "adventure" -> GameType.ADVENTURE;
            case "spectator" -> GameType.SPECTATOR;
            default -> GameType.SURVIVAL;
        };
        player.setGameMode(gameType);
    }

    private void handleInventory(ServerPlayer player, List<CallChain.Segment> segments, Map<String, Object> vars) {
        if (segments.isEmpty()) {
            LOGGER.warn("inventory requires a sub-method");
            return;
        }
        CallChain.Segment segment = segments.getFirst();
        switch (segment.name()) {
            case "clear" -> player.getInventory().clearContent();
            case "has" -> {
                if (segment.args().size() < 2) {
                    LOGGER.warn("inventory.has requires item ID and count");
                    return;
                }
                Item item = ScriptUtils.getItem(ScriptUtils.getStringArg(segment, 0, vars));
                int needed = (int) ScriptUtils.getNumberArg(segment, 1, vars);
                int count = 0;
                for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
                    if (stack.getItem() == item) {
                        count += stack.getCount();
                    }
                }
                vars.put("_last", count >= needed);
                LOGGER.debug("inventory.has = {}", vars.get("_last"));
            }
            default -> LOGGER.warn("Unknown inventory method: {}", segment.name());
        }
    }

    private void setHealth(ServerPlayer player, CallChain.Segment segment, Map<String, Object> vars) {
        if (segment.args().isEmpty()) {
            LOGGER.warn("setHealth requires max health value");
            return;
        }
        double maxHealth = ScriptUtils.getNumberArg(segment, 0, vars);
        AttributeInstance healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttr != null) {
            healthAttr.setBaseValue(Math.max(1.0, maxHealth));
            if (player.getHealth() > maxHealth) {
                player.setHealth((float) maxHealth);
            }
        }
    }

    private void addTag(ServerPlayer player, String tag) {
        if (tag.isEmpty()) {
            LOGGER.warn("addTag requires a non-empty tag name");
            return;
        }
        player.addTag(tag);
    }

    private void removeTag(ServerPlayer player, String tag) {
        if (tag.isEmpty()) {
            LOGGER.warn("removeTag requires a non-empty tag name");
            return;
        }
        player.removeTag(tag);
    }

    private void clearEffects(ServerPlayer player) {
        player.removeAllEffects();
    }
}