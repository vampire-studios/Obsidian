package io.github.vampirestudios.obsidian.addonapi.command;

import io.github.vampirestudios.obsidian.addonapi.model.ActionDefinition;
import net.minecraft.commands.Commands;
import net.minecraft.server.permissions.PermissionCheck;

import java.util.List;

public class CommandDefinition {
    public String id;                // "addon:open_main_screen"
    public List<String> path;        // ["addon", "menu", "open"]
    public String description;
    public int permission_level = 0;

    public List<CommandArgumentDef> arguments;
    public List<ActionDefinition> execute;

	public PermissionCheck getPermissionFromInt() {
		return switch(permission_level) {
			case 0 -> Commands.LEVEL_ALL;
			case 1 -> Commands.LEVEL_MODERATORS;
			case 2 -> Commands.LEVEL_GAMEMASTERS;
			case 3 -> Commands.LEVEL_ADMINS;
			case 4 -> Commands.LEVEL_OWNERS;
			default -> throw new IllegalStateException(STR."Unexpected value: \{permission_level}");
		};
	}
}
