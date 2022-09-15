package io.github.vampirestudios.obsidian.api.obsidian.ui;

import io.github.vampirestudios.obsidian.api.obsidian.NameInformation;

public class HUD extends UI {
    public NameInformation nameInformation;
    public Widget[] widgets;

    public static class Widget {
        public String type;
        public Position position;
        public String text;

        public static class Position {
            public int x;
            public int y;
            public int width;
            public int height;
        }
    }

}
