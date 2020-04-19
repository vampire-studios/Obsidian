package io.github.vampirestudios.obsidian.config;

import net.minecraft.util.Identifier;

import java.util.List;

public class AddonInformation {

    public String displayName;
    public String version;
    public String description;
    public Identifier id;
    public String namespace;
    public List<String> authors;

    public AddonInformation(String displayName, String version, String description, Identifier id, String namespace, List<String> authors) {
        this.displayName = displayName;
        this.version = version;
        this.description = description;
        this.id = id;
        this.namespace = namespace;
        this.authors = authors;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public Identifier getId() {
        return id;
    }

    public String getNamespace() {
        return namespace;
    }

    public List<String> getAuthors() {
        return authors;
    }

}