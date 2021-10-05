package io.github.vampirestudios.obsidian.configPack;

import java.util.List;
import java.util.Locale;

public class ObsidianAddonInfo {

    public String displayName;
    public String folderName = "";
    public String namespace = folderName.toLowerCase(Locale.ROOT).replace(" ", "_");
    public String version;
    public String description;
    public String license = "MIT";
    public List<String> authors;
    public List<Dependency> dependencies;
    public int addonVersion;
    public boolean hasAssets = false;
    public boolean hasData = false;

    public static class Dependency {
        public String namespace;
        public String version;
        public String type;
    }

}