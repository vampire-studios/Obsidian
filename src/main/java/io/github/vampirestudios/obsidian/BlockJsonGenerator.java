package io.github.vampirestudios.obsidian;

import net.minecraft.util.DyeColor;
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
        FileWriter fileWriter;
        if (!name.isEmpty()) {
            json = template.replace("$$", name + "_" + variant + "_" + type)
                    .replace("$folder$", folderName)
                    .replace("$big$", WordUtils.capitalizeFully(name + " " + variant + " " + type));
            File file = new File(folderName + "/" + name + "_" + variant + "_" + type + ".json");
            file.getParentFile().mkdirs();
            file.createNewFile();
            fileWriter = new FileWriter(folderName + "/" + name + "_" + variant + "_" + type + ".json");
        } else {
            json = template.replace("$$", variant + "_" + type)
                    .replace("$variant$", variant)
                    .replace("$big$", WordUtils.capitalizeFully(variant + " " + type));
            File file = new File(folderName + "/" + variant + "_" + type + ".json");
            file.getParentFile().mkdirs();
            file.createNewFile();
            fileWriter = new FileWriter(folderName + "/" + variant + "_" + type + ".json");
        }
        fileWriter.write(json);
        fileWriter.close();
    }

    public static void main(String[] args) throws IOException {
        String template = """
                {
                  "information": {
                    "name": {
                      "id": "origin_realms:$$",
                      "translated": {
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
        for (DyeColor color : DyeColor.values()) {
            generate(color.getName(), "mushroom", template, "fungal", "planks");
        }
        generate("", "mushroom", template, "fungal", "planks");
    }

}
