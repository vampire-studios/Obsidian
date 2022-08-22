package io.github.vampirestudios.obsidian.api.obsidian;

import blue.endless.jankson.api.DeserializationException;
import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface AddonModule {

    void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError, DeserializationException;

    default void initMealApi() throws FileNotFoundException {

    }

    default void initAppleSkin() throws FileNotFoundException {

    }

    String getType();

}
