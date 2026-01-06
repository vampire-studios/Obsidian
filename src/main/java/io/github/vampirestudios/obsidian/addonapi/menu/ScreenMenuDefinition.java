package io.github.vampirestudios.obsidian.addonapi.menu;

import java.util.List;

public class ScreenMenuDefinition extends MenuDefinition {

    public BackgroundDef background;
    public List<UiElementDef> elements;

    public static class BackgroundDef {
        public String mode;     // "none", "texture", "vanilla"
        public String texture;  // for "texture"
    }
}
