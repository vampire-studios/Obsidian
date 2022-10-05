package io.github.vampirestudios.obsidian.test;

import org.apache.commons.lang3.text.WordUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BlockJsonGenerator {

    public static void generate(String name, String template, String variant, String type) throws IOException {
        generate(name, variant + "_" + type, template, variant, type);
    }

    public static void generate(String name, String folderName, String template, String variant, String type) throws IOException {
        String json;
        String filePath;
        if (!name.isEmpty()) {
            json = template.replace("$$", name + "_" + variant + "_" + type)
                    .replace("$folder$", folderName)
                    .replace("$big$", WordUtils.capitalizeFully(name + " " + variant + " " + type));
            filePath = folderName + "/" + name + "_" + variant + "_" + type + ".json";
        } else {
            json = template.replace("$$", variant + "_" + type)
                    .replace("$variant$", variant)
                    .replace("$big$", WordUtils.capitalizeFully(variant + " " + type));
            filePath = folderName + "/" + variant + "_" + type + ".json";
        }
        generateFile(filePath, json);
    }

    private static void generateFile(String fileName, String json) throws IOException {
        File file = new File(fileName);
        file.getParentFile().mkdirs();
        file.createNewFile();
        FileWriter fileWriter = new FileWriter(fileName);
        fileWriter.write(json);
        fileWriter.close();
    }

    public static void main(String[] args) throws IOException {
        String template = """
                {
                  "information": {
                    "name": {
                      "id": "origin_realms:$$",
                      "translations": {
                        "en_us": "$big$"
                      }
                    },
                    "item_group": "minecraft:building_blocks",
                    "material": "minecraft:stone",
                    "sound_group": "minecraft:stone"
                  },
                  "display": {
                    "model": {
                      "parent": "block/cube_all",
                      "textures": {
                        "all": "origin_realms:block/$folder$/$$"
                      }
                    }
                  }
                }""";
        generate("", "mushroom2", template, "fungal", "planks");
    }

}
