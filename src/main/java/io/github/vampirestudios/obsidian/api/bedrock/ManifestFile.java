package io.github.vampirestudios.obsidian.api.bedrock;

import net.minecraft.resources.ResourceLocation;

public class ManifestFile extends BaseInformation {

    public Header header;
    public Modules[] modules;
    public Dependencies[] dependencies = new Dependencies[0];
    public Metadata metadata;

    public static class Header {
        public int[] base_game_version = new int[3];
        public String description;
        public boolean lock_template_options;
        public int[] min_engine_version = new int[3];
        public String name;
        public ResourceLocation identifier;
        public String uuid;
        public int[] version = new int[3];
    }

    public static class Modules {
        public String type;
        public String uuid;
        public String description;
        public int[] version;
    }

    public static class Dependencies {

        public String uuid;
        public int[] version;

    }

    public static class Metadata {

        public String[] authors;
        public String licence;
        public String url;

    }

}
