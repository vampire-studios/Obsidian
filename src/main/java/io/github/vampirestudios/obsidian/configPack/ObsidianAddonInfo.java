package io.github.vampirestudios.obsidian.configPack;

import blue.endless.jankson.annotation.SerializedName;
import net.fabricmc.loader.api.metadata.ContactInformation;
import net.fabricmc.loader.api.metadata.Person;

import java.util.ArrayList;
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
        public boolean has_assets = false;
        public String license = "";

        public List<Person> getAuthors() {
            List<Person> authors = new ArrayList<>();
            this.authors.forEach(author -> {
                authors.add(new Person() {
                    @Override
                    public String getName() {
                        return author;
                    }

                    @Override
                    public ContactInformation getContact() {
                        return ContactInformation.EMPTY;
                    }
                });
            });
            return authors;
        }
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
        HJSON,
        HOCON
    }

}