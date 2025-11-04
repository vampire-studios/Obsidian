package io.github.vampirestudios.obsidian.scripting.std;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.github.vampirestudios.obsidian.scripting.std.ScriptUtils.*;

public final class ObservablesCommandHandler implements CommandHandler {

    @Override
    public void handleCall(MinecraftServer server, List<CallChain.Segment> segments, Map<String, Object> vars) {
        if (segments.isEmpty()) return;
        var s = segments.get(0);

        switch (s.name()) {
            case "set" -> {
                // observables.set("name", expr)
                String key = asStr(eval(s.args().get(0), vars));
                Object val = eval(s.args().get(1), vars);
                ObsObservables.set(server, key, val);
            }
            case "get" -> {
                // observables.get("name") -> _last
                String key = asStr(eval(s.args().get(0), vars));
                vars.put("_last", ObsObservables.get(key));
            }
            case "define" -> {
                // observables.define("name", expr) (like set, but semantically "initial")
                String key = asStr(eval(s.args().get(0), vars));
                Object val = eval(s.args().get(1), vars);
                ObsObservables.define(server, key, val);
            }
            case "player" -> {
                // observables.player.set/get/define for the "context" player (sender/player)
                if (segments.size() < 2) return;
                var s2 = segments.get(1);
                ServerPlayer p = contextPlayer(vars);
                if (p == null) return;
                UUID who = p.getUUID();

                switch (s2.name()) {
                    case "set" -> {
                        String key = asStr(eval(s2.args().get(0), vars));
                        Object val = eval(s2.args().get(1), vars);
                        ObsObservables.setFor(server, who, key, val);
                    }
                    case "get" -> {
                        String key = asStr(eval(s2.args().get(0), vars));
                        vars.put("_last", ObsObservables.getFor(who, key));
                    }
                    case "define" -> {
                        String key = asStr(eval(s2.args().get(0), vars));
                        Object val = eval(s2.args().get(1), vars);
                        ObsObservables.defineFor(server, who, key, val);
                    }
                }
            }
            default -> { /* ignore unknown; keeps engine permissive */ }
        }
    }

    @Override
    public void handleCallWithBlock(MinecraftServer server, List<CallChain.Segment> segments, Stmt.Block body, Map<String, Object> vars) {
        // No block-style methods in observables.* for now
    }

    private static ServerPlayer contextPlayer(Map<String, Object> vars) {
        Object s = vars.get("sender");
        if (s instanceof ServerPlayer sp) return sp;
        Object p = vars.get("player");
        if (p instanceof ServerPlayer sp2) return sp2;
        return null;
    }
}
