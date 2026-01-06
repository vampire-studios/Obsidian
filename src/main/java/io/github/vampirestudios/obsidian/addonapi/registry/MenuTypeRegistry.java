package io.github.vampirestudios.obsidian.addonapi.registry;

import io.github.vampirestudios.obsidian.addonapi.menu.MenuDefinition;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class MenuTypeRegistry {

    public interface MenuOpener<T extends MenuDefinition> extends BiConsumer<ServerPlayer, T> { }

    private static final Map<String, MenuOpener<?>> OPENERS = new HashMap<>();

    public static <T extends MenuDefinition> void register(String type, MenuOpener<T> opener) {
        OPENERS.put(type, opener);
    }

    @SuppressWarnings("unchecked")
    public static <T extends MenuDefinition> void open(ServerPlayer player, T def) {
        if (def.type == null) return;
        MenuOpener<T> opener = (MenuOpener<T>) OPENERS.get(def.type);
        if (opener != null) {
            opener.accept(player, def);
        } else {
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                    "Unknown menu type: " + def.type));
        }
    }
}
