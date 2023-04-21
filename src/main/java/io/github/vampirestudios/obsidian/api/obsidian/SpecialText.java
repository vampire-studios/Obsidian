package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.GsonHelper;

public class SpecialText {

    public String text;
    @SerializedName("type")
    public String textType;
    public Map<String, String> translations = new HashMap<>();
    public String color = "";
    public String[] formatting = new String[0];

    public static Component of(JsonObject jsonObject) {
        SpecialText specialText = new SpecialText();
        if (GsonHelper.isStringValue(jsonObject, "text")) specialText.text = GsonHelper.getAsString(jsonObject, "text");
        if (GsonHelper.isStringValue(jsonObject, "type")) specialText.textType = GsonHelper.getAsString(jsonObject, "type");
        if (GsonHelper.isStringValue(jsonObject, "color")) specialText.color = GsonHelper.getAsString(jsonObject, "color");
        if (GsonHelper.isArrayNode(jsonObject, "formatting")) {
            JsonArray jsonArr = GsonHelper.getAsJsonArray(jsonObject, "formatting");
            String[] strArr = new String[jsonArr.size()];
            for(int i = 0; i < jsonArr.size(); i++) {
                JsonElement elem = jsonArr.get(i);
                if(GsonHelper.isStringValue(elem)) {
                    strArr[i] = elem.getAsString();
                } else {
                    throw new IllegalArgumentException("An element is not a string");
                }
            }
            specialText.formatting = strArr;
        }
        if(GsonHelper.isObjectNode(jsonObject, "translations")) {
            JsonObject translatedObject = GsonHelper.getAsJsonObject(jsonObject, "translations");
            for(Map.Entry<String, JsonElement> entry: translatedObject.entrySet()) {
                String lang = entry.getKey();
                JsonElement value = entry.getValue();
                if(GsonHelper.isStringValue(value)) {
                    String translation = value.getAsString();
                    specialText.translations.put(lang, translation);
                }
            }
        }
        return specialText.getName();
    }

    public Component getName() {
        String color1 = !this.color.isEmpty() && !this.color.isBlank() ? color.replace("#", "").replace("0x", "") : "ffffff";
        if (!text.isEmpty()) {
            if ("literal".equals(textType)) {
                MutableComponent literalText = Component.literal(text);
                for (String formatting1 : formatting) {
                    literalText = literalText.withStyle(ChatFormatting.getByName(formatting1));
                }
                if (!this.color.isEmpty() && !this.color.isBlank()) {
                    literalText = literalText.setStyle(literalText.getStyle().withColor(TextColor.parseColor(color1)));
                }
                return literalText;
            } else {
                MutableComponent translatableText = Component.translatable(text);
                for (String formatting1 : formatting) {
                    translatableText = translatableText.withStyle(ChatFormatting.getByName(formatting1));
                }
                if (!this.color.isEmpty() && !this.color.isBlank()) {
                    translatableText = translatableText.setStyle(translatableText.getStyle().withColor(TextColor.parseColor(color1)));
                }
                return translatableText;
            }
        } else {
            return Component.literal("");
        }
    }

}
