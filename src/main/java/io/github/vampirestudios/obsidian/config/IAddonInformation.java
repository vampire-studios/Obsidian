package io.github.vampirestudios.obsidian.config;

import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.util.Identifier;

import java.util.Collection;

public interface IAddonInformation {

    String getDisplayName();

    String getVersion();

    String getDescription();

    Identifier getId();

    String getNamespace();

    Collection<Person> getAuthors();

}