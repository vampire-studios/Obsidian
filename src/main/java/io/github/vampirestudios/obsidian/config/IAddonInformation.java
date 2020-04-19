package io.github.vampirestudios.obsidian.config;

import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.util.Identifier;

import java.util.Collection;

public interface IAddonInformation {

    String getDisplayName();
    public String getVersion();
    public String getDescription();
    public Identifier getId();
    public String getNamespace();
    public Collection<Person> getAuthors();

}