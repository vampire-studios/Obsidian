package io.github.vampirestudios.obsidian.addonapi;

import java.util.List;

public class ScreenMenuDefinition extends MenuDefinition {
    public BackgroundDef background;
    public List<UiElementDef> elements = List.of();

    public static class BackgroundDef {
        public String mode;       // "texture", "none", "vanilla"
        public String texture;    // for "texture"
    }
}