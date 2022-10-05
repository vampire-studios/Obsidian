package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.JsonHelper;

import java.util.HashMap;
import java.util.Map;

public class SpecialText {

    public String text;
    @SerializedName("type")
    public String textType;
    public Map<String, String> translations = new HashMap<>();
    public String color = "";
    public String[] formatting = new String[0];

    public static Text of(JsonObject jsonObject) {
        SpecialText specialText = new SpecialText();
        if (JsonHelper.hasString(jsonObject, "text")) specialText.text = JsonHelper.getString(jsonObject, "text");
        if (JsonHelper.hasString(jsonObject, "type")) specialText.textType = JsonHelper.getString(jsonObject, "type");
        if (JsonHelper.hasString(jsonObject, "color")) specialText.color = JsonHelper.getString(jsonObject, "color");
        if (JsonHelper.hasArray(jsonObject, "formatting")) {
            JsonArray jsonArr = JsonHelper.getArray(jsonObject, "formatting");
            String[] strArr = new String[jsonArr.size()];
            for(int i = 0; i < jsonArr.size(); i++) {
                JsonElement elem = jsonArr.get(i);
                if(JsonHelper.isString(elem)) {
                    strArr[i] = elem.getAsString();
                } else {
                    throw new IllegalArgumentException("An element is not a string");
                }
            }
            specialText.formatting = strArr;
        }
        if(JsonHelper.hasJsonObject(jsonObject, "translations")) {
            JsonObject translatedObject = JsonHelper.getObject(jsonObject, "translations");
            for(Map.Entry<String, JsonElement> entry: translatedObject.entrySet()) {
                String lang = entry.getKey();
                JsonElement value = entry.getValue();
                if(JsonHelper.isString(value)) {
                    String translation = value.getAsString();
                    specialText.translations.put(lang, translation);
                }
            }
        }
        return specialText.getName();
    }

    public Text getName() {
        String color1 = !this.color.isEmpty() && !this.color.isBlank() ? color.replace("#", "").replace("0x", "") : "ffffff";
        if (!text.isEmpty()) {
            if ("literal".equals(textType)) {
                MutableText literalText = Text.literal(text);
                for (String formatting1 : formatting) {
                    literalText = literalText.formatted(Formatting.byName(formatting1));
                }
                if (!this.color.isEmpty() && !this.color.isBlank()) {
                    literalText = literalText.setStyle(literalText.getStyle().withColor(TextColor.parse(color1)));
                }
                return literalText;
            } else {
                MutableText translatableText = Text.translatable(text);
                for (String formatting1 : formatting) {
                    translatableText = translatableText.formatted(Formatting.byName(formatting1));
                }
                if (!this.color.isEmpty() && !this.color.isBlank()) {
                    translatableText = translatableText.setStyle(translatableText.getStyle().withColor(TextColor.parse(color1)));
                }
                return translatableText;
            }
        } else {
            return Text.literal("");
        }
    }

}
