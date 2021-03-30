//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.github.vampirestudios.obsidian;

import net.minecraft.resource.*;
import net.minecraft.resource.ResourcePackProfile.Factory;
import net.minecraft.resource.ResourcePackProfile.InsertionPosition;

import java.io.File;
import java.io.FileFilter;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FileResourcePackProvider implements ResourcePackProvider {
    private static final FileFilter POSSIBLE_PACK = (file) -> file.isDirectory() && (new File(file, "addon.info.pack")).isFile();
    private final File packsFolder;
    private final ResourcePackSource resourcePackSource;

    public FileResourcePackProvider(File packsFolder, ResourcePackSource resourcePackSource) {
        this.packsFolder = packsFolder;
        this.resourcePackSource = resourcePackSource;
    }

    public void register(Consumer<ResourcePackProfile> consumer, Factory factory) {
        if (!this.packsFolder.isDirectory()) {
            this.packsFolder.mkdirs();
        }

        File[] files = this.packsFolder.listFiles(POSSIBLE_PACK);
        if (files != null) {
            for (File file : files) {
                String string = "file/" + file.getName();
                ResourcePackProfile resourcePackProfile = ResourcePackProfile.of(string, false, this.createResourcePack(file), factory, InsertionPosition.TOP, this.resourcePackSource);
                if (resourcePackProfile != null) {
                    consumer.accept(resourcePackProfile);
                }
            }

        }
    }

    private Supplier<ResourcePack> createResourcePack(File file) {
        return file.isDirectory() ? () -> new DirectoryResourcePack(file) : () -> new ZipResourcePack(file);
    }

}