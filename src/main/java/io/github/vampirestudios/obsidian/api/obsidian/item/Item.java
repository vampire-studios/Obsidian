package io.github.vampirestudios.obsidian.api.obsidian.item;

import blue.endless.jankson.annotation.SerializedName;
import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.BaseGson;
import io.github.vampirestudios.obsidian.api.obsidian.ItemDisplayInformation;
import io.github.vampirestudios.obsidian.api.obsidian.SpecialText;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.Consumer;

public class Item {
    public ItemType type;
    public ItemInformation information;
    public ItemDisplayInformation rendering;
    public UseActions useActions;
    public DataComponentPatch components;
    public List<Object> lore = new ArrayList<>();
    public Map<ResourceLocation, ResourceLocation> drops = new HashMap<>();
    public Map<String, List<Map<String, Object>>> events = new HashMap<>();

    @SerializedName("menu_config")
    @com.google.gson.annotations.SerializedName("menu_config")
    public CustomMenuConfig menuConfig;

    public List<SpecialText> getLore() {
        List<SpecialText> lore1 = new ArrayList<>();
        for (Object o : lore) {
            SpecialText specialText = switch (o) {
                case String s -> {
                    SpecialText specialText1 = new SpecialText();
                    specialText1.text = s;
                    yield specialText1;
                }
                case SpecialText specialText1 -> specialText1;
                case JsonObject object -> BaseGson.GSON.fromJson(object, SpecialText.class);
				default -> throw new IllegalStateException(STR."Unexpected value: \{o}");
			};
            lore1.add(specialText);
        }
        return lore1;
    }

    public void addLore(Consumer<Component> tooltip) {
        if (lore != null && !lore.isEmpty()) {
            for (SpecialText text : getLore()) {
                tooltip.accept(text.getName());
            }
        }
    }

    public List<Map<String, Object>> getEventActions(String event) {
        return events.getOrDefault(event, Collections.emptyList());
    }

    public enum ItemType {
        SHEARS,
        BUNDLE,
        CUSTOM_MENU
    }
}