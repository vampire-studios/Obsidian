package io.github.vampirestudios.obsidian.scripting.std;

import net.minecraft.server.MinecraftServer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ObsObservables {
    private ObsObservables() {}

    // GLOBAL values
    private static final Map<String, Object> GLOBAL = new ConcurrentHashMap<>();
    // PER-PLAYER: playerUUID -> (name -> value)
    private static final Map<UUID, Map<String, Object>> PER_PLAYER = new ConcurrentHashMap<>();

    // Watches
    private static final Map<String, List<Watcher>> GLOBAL_WATCHES = new ConcurrentHashMap<>();
    private static final Map<String, List<Watcher>> PLAYER_WATCHES = new ConcurrentHashMap<>();

    /** Attach a GLOBAL observable (define or override) */
    public static void define(MinecraftServer srv, String name, Object value) {
        Object prev = GLOBAL.put(name, cloneValue(value));
        if (!Objects.equals(prev, value)) fireGlobal(srv, name, prev, value);
    }

    /** Attach a PLAYER observable default; not required, setFor will create map on demand. */
    public static void defineFor(MinecraftServer srv, UUID who, String name, Object value) {
        var map = PER_PLAYER.computeIfAbsent(who, __ -> new ConcurrentHashMap<>());
        Object prev = map.put(name, cloneValue(value));
        if (!Objects.equals(prev, value)) firePlayer(srv, who, name, prev, value);
    }

    public static Object get(String name) { return GLOBAL.get(name); }
    public static Object getFor(UUID who, String name) {
        var m = PER_PLAYER.get(who); return (m == null) ? null : m.get(name);
    }

    public static void set(MinecraftServer srv, String name, Object value) {
        Object prev = GLOBAL.put(name, cloneValue(value));
        if (!Objects.equals(prev, value)) fireGlobal(srv, name, prev, value);
    }
    public static void setFor(MinecraftServer srv, UUID who, String name, Object value) {
        var map = PER_PLAYER.computeIfAbsent(who, __ -> new ConcurrentHashMap<>());
        Object prev = map.put(name, cloneValue(value));
        if (!Objects.equals(prev, value)) firePlayer(srv, who, name, prev, value);
    }

    /** Register a GLOBAL watch callback. */
    public static void watch(String name, WatchCallback cb) {
        GLOBAL_WATCHES.computeIfAbsent(name, __ -> new ArrayList<>())
                .add(new Watcher(cb));
    }

    /** Register a PLAYER watch callback. */
    public static void watchPlayer(String name, PlayerWatchCallback cb) {
        PLAYER_WATCHES.computeIfAbsent(name, __ -> new ArrayList<>())
                .add(new Watcher(cb));
    }

    /* ---- internals ---- */

    private static void fireGlobal(MinecraftServer srv, String name, Object prev, Object cur) {
        var list = GLOBAL_WATCHES.get(name);
        if (list == null) return;
        for (Watcher w : List.copyOf(list)) {
            if (w.globalCb != null) w.globalCb.onChange(srv, name, prev, cur);
        }
    }

    private static void firePlayer(MinecraftServer srv, UUID who, String name, Object prev, Object cur) {
        var list = PLAYER_WATCHES.get(name);
        if (list == null) return;
        for (Watcher w : List.copyOf(list)) {
            if (w.playerCb != null) w.playerCb.onChange(srv, who, name, prev, cur);
        }
    }

    private static Object cloneValue(Object v) {
        // Immutable primitives/Strings fine; extend if you add lists/maps later.
        return v;
    }

    public static boolean isObservable(String name) {
        return GLOBAL.containsKey(name);
    }
    public static boolean hasFor(UUID who, String name) {
        var m = PER_PLAYER.get(who);
        return m != null && m.containsKey(name);
    }

    private record Watcher(WatchCallback globalCb, PlayerWatchCallback playerCb) {
        Watcher(WatchCallback cb) { this(cb, null); }
        Watcher(PlayerWatchCallback cb) { this(null, cb); }
    }

    /** GLOBAL change */
    @FunctionalInterface public interface WatchCallback {
        void onChange(MinecraftServer srv, String name, Object oldV, Object newV);
    }

    /** PLAYER change */
    @FunctionalInterface public interface PlayerWatchCallback {
        void onChange(MinecraftServer srv, UUID who, String name, Object oldV, Object newV);
    }
}