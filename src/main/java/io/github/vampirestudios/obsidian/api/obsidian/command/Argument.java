package io.github.vampirestudios.obsidian.api.obsidian.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

public class Argument {

    public String name;
    public String type;

    public ArgumentType<?> getBasicArgumentType() {
        switch(type) {
            case "string":
                return StringArgumentType.greedyString();
            case "integer":
                return IntegerArgumentType.integer();
            default:
                return null;
        }
    }

}
