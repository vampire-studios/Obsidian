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
        String template = "{\n" +
                "                  \"information\": {\n" +
                "                    \"name\": {\n" +
                "                      \"id\": \"origin_realms:$$\",\n" +
                "                      \"translated\": {\n" +
                "                        \"en_us\": \"$big$\"\n" +
                "                      }\n" +
                "                    },\n" +
                "                    \"item_group\": \"minecraft:building_blocks\",\n" +
                "                    \"material\": \"minecraft:stone\",\n" +
                "                    \"sound_group\": \"minecraft:stone\"\n" +
                "                  },\n" +
                "                  \"display\": {\n" +
                "                    \"model\": {\n" +
                "                      \"parent\": \"block/cube_all\",\n" +
                "                      \"textures\": {\n" +
                "                        \"all\": \"origin_realms:block/$folder$/$$\"\n" +
                "                      }\n" +
                "                    }\n" +
                "                  }\n" +
                "                }";
        for (DyeColor color : DyeColor.values()) {
            generate(color.getName(), "mushroom", template, "fungal", "planks");
        }
        generate("", "mushroom", template, "fungal", "planks");
    }

}
