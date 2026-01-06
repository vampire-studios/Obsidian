package io.github.vampirestudios.obsidian.addonapi.registry;

import io.github.vampirestudios.obsidian.addonapi.command.CommandDefinition;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AddonCommandRegistry {

    private static final Map<Identifier, CommandDefinition> COMMANDS = new HashMap<>();

    public static void put(Identifier id, CommandDefinition def) {
        COMMANDS.put(id, def);
    }

    public static CommandDefinition get(Identifier id) {
        return COMMANDS.get(id);
    }

    public static Collection<CommandDefinition> all() {
        return COMMANDS.values();
    }

    public static void clear() {
        COMMANDS.clear();
    }
}
