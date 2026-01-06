package io.github.vampirestudios.obsidian.scripting.std;

import io.github.vampirestudios.obsidian.client.GuiBridge;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class GuiCommandHandler implements CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger("GuiCommandHandler");

    @Override
    public void handleCall(MinecraftServer server, List<CallChain.Segment> segments, Map<String, Object> vars) {
        if (segments.isEmpty()) { LOGGER.warn("gui.* requires a method"); return; }
        CallChain.Segment s = segments.getFirst();

        // actor: default to sender (required to check ownership)
        Object o = vars.get("sender");
        if (!(o instanceof ServerPlayer player)) {
            LOGGER.warn("gui.* requires a player context (sender)");
            return;
        }

        try {
            switch (s.name()) {
                case "create" -> {
                    // gui.create(rows, title)
                    int rows     = (int) ScriptUtils.getNumberArg(s, 0, vars);
                    String title = s.args().size() >= 2 ? ScriptUtils.getStringArg(s, 1, vars) : "";
                    String id = GuiBridge.create(player, rows, Component.literal(title));
                    vars.put("_last_gui", id);
                    vars.put("_last", id);
                }

                case "open" -> {
                    // New: gui.open(id)
                    // Legacy: gui.open(rows, title) — still supported
                    if (!s.args().isEmpty() && ScriptUtils.isStringLike(s.args().getFirst(), vars)) {
                        String id = ScriptUtils.getStringArg(s, 0, vars);
                        GuiBridge.open(id, player);
                        vars.put("_last", true);
                    } else {
                        int rows     = (int) ScriptUtils.getNumberArg(s, 0, vars);
                        String title = s.args().size() >= 2 ? ScriptUtils.getStringArg(s, 1, vars) : "";
                        String id = GuiBridge.create(player, rows, Component.literal(title));
                        GuiBridge.open(id, player);
                        vars.put("_last_gui", id);
                        vars.put("_last", id);
                    }
                }

                case "title" -> {
                    String id = ScriptUtils.getStringArg(s, 0, vars);
                    String title = ScriptUtils.getStringArg(s, 1, vars);
                    var g = GuiBridge.get(id);
                    if (g != null && g.getPlayer() == player) {
                        g.setTitle(Component.literal(title));
                        g.open(); // force client update
                    }
                }

                case "set" -> {
                    String id   = ScriptUtils.getStringArg(s, 0, vars);
                    int slot    = (int) ScriptUtils.getNumberArg(s, 1, vars);
                    String itm  = ScriptUtils.getStringArg(s, 2, vars);
                    int count   = s.args().size() >= 4 ? (int) ScriptUtils.getNumberArg(s, 3, vars) : 1;
                    Map<String,Object> comps = null;
                    if (s.args().size() >= 5) {
                        Object obj = ScriptUtils.eval(s.args().get(4), vars);
                        if (obj instanceof Map<?,?> m) comps = (Map<String, Object>) m;
                    }
                    GuiBridge.set(id, slot, itm, count, comps, player);
                }

                case "fill" -> {
                    String id  = ScriptUtils.getStringArg(s, 0, vars);
                    int from   = (int) ScriptUtils.getNumberArg(s, 1, vars);
                    int to     = (int) ScriptUtils.getNumberArg(s, 2, vars);
                    String itm = ScriptUtils.getStringArg(s, 3, vars);
                    int count  = s.args().size() >= 5 ? (int) ScriptUtils.getNumberArg(s, 4, vars) : 1;
                    Map<String,Object> comps = null;
                    if (s.args().size() >= 6) {
                        Object obj = ScriptUtils.eval(s.args().get(5), vars);
                        if (obj instanceof Map<?,?> m) comps = (Map<String, Object>) m;
                    }
                    int a = Math.max(0, Math.min(from, to));
                    int b = Math.max(a, Math.max(from, to));
                    for (int i = a; i <= b; i++) GuiBridge.set(id, i, itm, count, comps, player);
                }

                case "setRaw" -> {
                    String id   = ScriptUtils.getStringArg(s, 0, vars);
                    int slot    = (int) ScriptUtils.getNumberArg(s, 1, vars);
                    String itm  = ScriptUtils.getStringArg(s, 2, vars);
                    int count   = s.args().size() >= 4 ? (int) ScriptUtils.getNumberArg(s, 3, vars) : 1;

                    var gui = GuiBridge.get(id);
                    if (gui == null || gui.getPlayer() != player) return;
                    if (slot < 0 || slot >= gui.getSize()) throw new IllegalArgumentException("slot OOB: " + slot);

                    var key = Identifier.parse(itm);
                    var item = BuiltInRegistries.ITEM.getValue(key);
                    if (item == null) throw new IllegalArgumentException("Unknown item: " + itm);

                    ItemStack stack = new ItemStack(item, Math.min(64, Math.max(1, count)));
                    gui.setSlot(slot, new eu.pb4.sgui.api.elements.GuiElementBuilder(stack).build());
                }

                case "close" -> {
                    // gui.close(id)
                    String id = ScriptUtils.getStringArg(s, 0, vars);
                    GuiBridge.close(id, player);
                }

                default -> LOGGER.warn("Unknown gui method: {}", s.name());
            }
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Error in gui.{}: {}", s.name(), ex.getMessage());
        }
    }

    @Override
    public void handleCallWithBlock(MinecraftServer server, List<CallChain.Segment> segments, Stmt.Block body, Map<String, Object> vars) {
        // Not used yet; could support: gui.build(rows,title) { set(...); set(...); }
        LOGGER.warn("gui.* with trailing block is not implemented.");
    }
}