package io.github.vampirestudios.obsidian.addonModules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.command.Command;
import io.github.vampirestudios.obsidian.minecraft.obsidian.CommandImpl;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ConfigHelper.*;

public class Commands implements AddonModule {
    @Override
    public void init(File file, ModIdAndAddonPath id) throws FileNotFoundException {
        Command command = Obsidian.GSON.fromJson(new FileReader(file), Command.class);
        try {
            if(command == null) return;
            // Using a lambda
            CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
                // This command will be registered regardless of the server being dedicated or integrated
                CommandImpl.register(command, dispatcher);
            });
            register(COMMANDS, "command", command.name, command);
        } catch (Exception e) {
            failedRegistering("command", command.name, e);
        }
    }

    @Override
    public String getType() {
        return "commands";
    }
}
