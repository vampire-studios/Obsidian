package io.github.vampirestudios.obsidian.addonapi;

import io.github.vampirestudios.obsidian.addonapi.model.TextComponentDef;

public class MenuDefinition {
    public String id;               // "addon:main_screen"
    public String type;             // "screen", "chest", "chat_list" etc.
    public TextComponentDef title;
}
