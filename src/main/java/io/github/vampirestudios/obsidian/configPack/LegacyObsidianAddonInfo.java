package io.github.vampirestudios.obsidian.configPack;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LegacyObsidianAddonInfo extends BaseAddonInfo {

    public String displayName = "";
    public int addonVersion;
    public String version;
    public String folderName = "";
    public String namespace = folderName.toLowerCase(Locale.ROOT).replace(" ", "_");
    public String description;
    public List<Map<String, Dependency>> requires;
    public List<Map<String, Dependency>> breaks;
    public List<Map<String, Dependency>> optional;
    public String license = "MIT";
    public List<String> authors;
    public List<Dependency> dependencies;

    public static class Dependency {
        public String namespace;
        public String version;
        public String type;
    }

}