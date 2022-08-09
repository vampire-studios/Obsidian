package io.github.vampirestudios.obsidian.configPack;

import blue.endless.jankson.annotation.SerializedName;

import java.util.List;
import java.util.Map;

public class ObsidianAddonInfo extends BaseAddonInfo {

    public int version;
    public Format format = Format.JSON;
    public Addon addon;
    public List<Map<String, Dependency>> requires;
    public List<Map<String, Dependency>> breaks;
    public List<Map<String, Dependency>> optional;

    public static class Addon {
        public String id;
        public String version;
        public String name;
        @SerializedName("folder_name") public String folderName;
        public String description;
        public List<String> authors;
        public String license = "";
    }

    public static class Dependency {
        public String version;
        public Type type = Type.ADDON;

        public enum Type {
            MOD,
            ADDON
        }
    }

    public enum Format {
        JSON,
        JSON5,
        TOML,
        YAML,
        HJSON
    }

}