package io.github.vampirestudios.obsidian.addon_modules;

import blue.endless.jankson.api.SyntaxError;
import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.IAddonPack;
import io.github.vampirestudios.obsidian.api.obsidian.world.Tree;
import io.github.vampirestudios.obsidian.registry.ContentRegistries;
import io.github.vampirestudios.obsidian.utils.BasicAddonInfo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Trees implements AddonModule {
    @Override
    public void init(IAddonPack addon, File file, BasicAddonInfo id) throws IOException, SyntaxError {
        Tree tree = Obsidian.GSON.fromJson(new FileReader(file), Tree.class);
        try {
            if (tree == null) return;

            register(ContentRegistries.TREES, "tree", tree.id, tree);
        } catch (Exception e) {
            failedRegistering("tree", tree.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "world/tree";
    }
}
