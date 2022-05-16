package io.github.vampirestudios.obsidian.addon_modules;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.world.Tree;
import io.github.vampirestudios.obsidian.configPack.ObsidianAddon;
import io.github.vampirestudios.obsidian.utils.ModIdAndAddonPath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static io.github.vampirestudios.obsidian.configPack.ObsidianAddonLoader.*;

public class Trees implements AddonModule {
    @Override
    public void init(ObsidianAddon addon, File file, ModIdAndAddonPath id) throws FileNotFoundException {
        Tree tree = Obsidian.GSON.fromJson(new FileReader(file), Tree.class);
        try {
            if (tree == null) return;

            register(TREES, "tree", tree.id, tree);
        } catch (Exception e) {
            failedRegistering("tree", tree.id.toString(), e);
        }
    }

    @Override
    public String getType() {
        return "world/tree";
    }
}
