package io.github.vampirestudios.obsidian.configPack;

import com.google.gson.JsonElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.*;
import net.fabricmc.loader.metadata.EntrypointMetadata;
import net.fabricmc.loader.metadata.LoaderModMetadata;
import net.fabricmc.loader.metadata.NestedJarEntry;
import net.fabricmc.loader.util.version.StringVersion;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class ObsidianAddonModMetadata implements LoaderModMetadata {

    public ObsidianAddon addon;

    public ObsidianAddonModMetadata(ObsidianAddon addon) {
        this.addon = addon;
    }

    @Override
    public int getSchemaVersion() {
        return 2;
    }

    @Override
    public Map<String, String> getLanguageAdapterDefinitions() {
        return Collections.emptyMap();
    }

    @Override
    public Collection<NestedJarEntry> getJars() {
        return null;
    }

    @Override
    public Collection<String> getMixinConfigs(EnvType type) {
        return null;
    }

    @Override
    public String getAccessWidener() {
        return null;
    }

    @Override
    public boolean loadsInEnvironment(EnvType type) {
        return true;
    }

    @Override
    public Collection<String> getOldInitializers() {
        return Collections.emptySet();
    }

    @Override
    public List<EntrypointMetadata> getEntrypoints(String type) {
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getEntrypointKeys() {
        return Collections.emptySet();
    }

    @Override
    public void emitFormatWarnings(Logger logger) {

    }

    @Override
    public String getType() {
        return "builtin";
    }

    @Override
    public String getId() {
        return addon.getConfigPackInfo().namespace;
    }

    @Override
    public Version getVersion() {
        return new StringVersion(addon.getConfigPackInfo().version);
    }

    @Override
    public ModEnvironment getEnvironment() {
        return ModEnvironment.UNIVERSAL;
    }

    @Override
    public Collection<ModDependency> getDepends() {
        return Collections.emptyList();
    }

    @Override
    public Collection<ModDependency> getRecommends() {
        return Collections.emptySet();
    }

    @Override
    public Collection<ModDependency> getSuggests() {
        return Collections.emptySet();
    }

    @Override
    public Collection<ModDependency> getConflicts() {
        return Collections.emptySet();
    }

    @Override
    public Collection<ModDependency> getBreaks() {
        return Collections.emptySet();
    }

    @Override
    public String getName() {
        return addon.getDisplayNameObsidian();
    }

    @Override
    public String getDescription() {
        return addon.getConfigPackInfo().description;
    }

    @Override
    public Collection<Person> getAuthors() {
        if (!addon.getConfigPackInfo().authors.isEmpty()) {
            Collection<Person> authors = new ArrayList<>();
            addon.getConfigPackInfo().authors.forEach(s -> authors.add(new Person() {
                @Override
                public String getName() {
                    return s;
                }

                @Override
                public ContactInformation getContact() {
                    return ContactInformation.EMPTY;
                }
            }));
            return authors;
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public Collection<Person> getContributors() {
        if (!addon.getConfigPackInfo().authors.isEmpty()) {
            Collection<Person> authors = new ArrayList<>();
            addon.getConfigPackInfo().authors.forEach(s -> authors.add(new Person() {
                @Override
                public String getName() {
                    return s;
                }

                @Override
                public ContactInformation getContact() {
                    return ContactInformation.EMPTY;
                }
            }));
            return authors;
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public ContactInformation getContact() {
        return new ContactInformation() {
            final Map<String, String> contact = new HashMap<>();
            {
                contact.put("homepage", "https://vampirestudios.netlify.app/pages/obsidian.html");
                contact.put("sources", "https://github.com/vampire-studios/Obsidian");
                contact.put("issues", "https://github.com/vampire-studios/Obsidian/issues");
            }

            @Override
            public Optional<String> get(String key) {
                return Optional.ofNullable(contact.get(key));
            }

            @Override
            public Map<String, String> asMap() {
                return contact;
            }
        };
    }

    @Override
    public Collection<String> getLicense() {
        return Collections.singleton("UwU Licence");
    }

    @Override
    public Optional<String> getIconPath(int size) {
        return Optional.of("pack.png");
    }

    @Override
    public boolean containsCustomValue(String key) {
        return key.equals("modmenu:parent");
    }

    @Override
    public CustomValue getCustomValue(String key) {
        return new CustomValue() {
            @Override
            public CvType getType() {
                return CvType.STRING;
            }

            @Override
            public CvObject getAsObject() {
                return null;
            }

            @Override
            public CvArray getAsArray() {
                return null;
            }

            @Override
            public String getAsString() {
                return "obsidian";
            }

            @Override
            public Number getAsNumber() {
                return null;
            }

            @Override
            public boolean getAsBoolean() {
                return false;
            }
        };
    }

    @Override
    public Map<String, CustomValue> getCustomValues() {
        return Collections.singletonMap("modmenu:parent", new CustomValue() {
            @Override
            public CvType getType() {
                return CvType.STRING;
            }

            @Override
            public CvObject getAsObject() {
                return null;
            }

            @Override
            public CvArray getAsArray() {
                return null;
            }

            @Override
            public String getAsString() {
                return "obsidian";
            }

            @Override
            public Number getAsNumber() {
                return null;
            }

            @Override
            public boolean getAsBoolean() {
                return false;
            }
        });
    }

    @Override
    public boolean containsCustomElement(String key) {
        return false;
    }

    @Override
    public JsonElement getCustomElement(String key) {
        return null;
    }
}