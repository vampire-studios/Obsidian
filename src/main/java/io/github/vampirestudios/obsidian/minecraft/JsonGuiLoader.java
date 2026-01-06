/*
package io.github.vampirestudios.obsidian.minecraft;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PanelWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class JsonGuiLoader {
    public static AbstractWidget loadWidget(JsonObject json) {
        String type = json.get("type").getAsString();

		return switch (type) {
			case "panel" -> loadPanelWidget(json);
			case "button" -> loadButton(json);
            case "spacer" -> loadSpacer(json); // Add support for SpacerElement
			default -> null;
		};
	}

    private static SpacerElement loadSpacer(JsonObject json) {
        int width = parseDimension(json, "width", 0);
        int height = parseDimension(json, "height", 0);

        // Optionally read position, defaulting to 0 if not present
        int x = readIntOrAuto(json.has("x") ? json.get("x") : null, 0);
        int y = readIntOrAuto(json.has("y") ? json.get("y") : null, 0);

        return new SpacerElement(x, y, width, height);
    }

    private static PanelWidget loadPanelWidget(JsonObject json) {
        int x = readIntOrAuto(json.has("x") ? json.get("x") : null, 0);
        int y = readIntOrAuto(json.has("y") ? json.get("y") : null, 0);

        int width = parseDimension(json, "width", */
/* fallback *//*
 300);
        int height = parseDimension(json, "height", */
/* fallback *//*
 200);
        int spacing = parseDimension(json, "spacing", */
/* fallback *//*
 10);

        PanelWidget panel = new PanelWidget(x, y, width, height, spacing);

        if (json.has("background")) {
            String bgTex = json.get("background").getAsString();
            panel.setTexture(Identifier.parse(bgTex));
        }

        if (json.has("layout")) {
            JsonObject layoutObj = json.getAsJsonObject("layout");
            if ("grid".equals(layoutObj.get("type").getAsString()) && layoutObj.has("grid")) {
                panel.setRowHelper(createGridLayout(layoutObj.getAsJsonObject("grid")));
            }
        }

        if (json.has("children")) {
            JsonArray children = json.getAsJsonArray("children");
            for (JsonElement childElem : children) {
                AbstractWidget child = loadWidget(childElem.getAsJsonObject());
                panel.addChild(child);
            }
        }

        return panel;
    }

    private static GridLayout.RowHelper createGridLayout(JsonObject object) {
        GridLayout gridLayout = new GridLayout();
        LayoutSettings settings = gridLayout.defaultCellSetting();

        // Apply padding settings
        if (object.has("padding")) {
            JsonObject paddingObj = object.getAsJsonObject("padding");
            applyPadding(paddingObj, settings);
        }

        // Apply alignment settings
        if (object.has("align")) {
            JsonObject alignObj = object.getAsJsonObject("align");
            applyAlignment(alignObj, settings);
        }

        return gridLayout.createRowHelper(object.get("columns").getAsInt());
    }

    */
/**
     * Applies padding settings based on JSON object.
     *//*

    private static void applyPadding(JsonObject paddingObj, LayoutSettings settings) {
        settings.paddingHorizontal(paddingObj.has("horizontal") ? paddingObj.get("horizontal").getAsInt() : 0);
        settings.paddingVertical(paddingObj.has("vertical") ? paddingObj.get("vertical").getAsInt() : 0);
        settings.paddingLeft(paddingObj.has("left") ? paddingObj.get("left").getAsInt() : 0);
        settings.paddingTop(paddingObj.has("top") ? paddingObj.get("top").getAsInt() : 0);
        settings.paddingRight(paddingObj.has("right") ? paddingObj.get("right").getAsInt() : 0);
        settings.paddingBottom(paddingObj.has("bottom") ? paddingObj.get("bottom").getAsInt() : 0);
    }

    */
/**
     * Applies alignment settings based on JSON object.
     *//*

    private static void applyAlignment(JsonObject alignObj, LayoutSettings settings) {
        if (alignObj.has("horizontal")) {
            String horizontalAlignment = alignObj.get("horizontal").getAsString();
            switch (horizontalAlignment) {
                case "center": settings.alignHorizontallyCenter(); break;
                case "left": settings.alignHorizontallyLeft(); break;
                case "right": settings.alignHorizontallyRight(); break;
            }
        }

        if (alignObj.has("vertical")) {
            String verticalAlignment = alignObj.get("vertical").getAsString();
            switch (verticalAlignment) {
                case "top": settings.alignVerticallyTop(); break;
                case "middle": settings.alignVerticallyMiddle(); break;
                case "bottom": settings.alignVerticallyBottom(); break;
            }
        }
    }

    private static Button loadButton(JsonObject json) {
        int x = parseDimension(json, "x", 0);
        int y = parseDimension(json, "y", 0);
        int w = parseDimension(json, "width", 100);
        int h = parseDimension(json, "height", 20);
        String text = json.get("text").getAsString();

        String onClickCmd = json.has("on_click") ? json.get("on_click").getAsString() : null;

        Button.OnPress callback = (btn) -> {
            if (onClickCmd != null) {
                // interpret or call your code
                runGuiCommand(onClickCmd);
            }
        };
        return Button.builder(Component.literal(text), callback)
				.bounds(x, y, w, h)
                .build();
    }

    private static void runGuiCommand(String cmd) {
        switch (cmd) {
//            case "startGame" -> Minecraft.getInstance().startIntegratedServer("NewWorldName");
//            case "openSettings" -> Minecraft.getInstance().setScreen(new OptionsScreen(...));
            case "quitGame" -> Minecraft.getInstance().stop();
            default -> System.out.println("Unknown GUI command: " + cmd);
        }
    }

    */
/**
     * Parses a dimension field (like "width" or "height") from the given JSON object.
     * The field can be:
     *   - An integer (e.g. 200)
     *   - A string "auto"
     *   - Missing or invalid
     *
     * @param json          The JSON object containing the field
     * @param field         The name of the field (e.g., "width")
     * @param defaultValue  The integer to return if the field is missing or invalid
     * @return an integer dimension or -1 if "auto"
     *//*

    private static int parseDimension(JsonObject json, String field, int defaultValue) {
        if (!json.has(field)) {
            // Field missing => return defaultValue
            return defaultValue;
        }

        JsonElement element = json.get(field);

        // If it's a string, check for "auto" or parse as number
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            String strVal = element.getAsString();
            if (strVal.equalsIgnoreCase("auto")) {
                // Return -1 to indicate "auto" to the layout system
                return -1;
            } else {
                // Try to parse it as an integer string (e.g. "100")
                try {
                    return Integer.parseInt(strVal);
                } catch (NumberFormatException e) {
                    // If it's not "auto" nor a valid integer, fallback
                    return defaultValue;
                }
            }
        }
        // If it's a number, just return as int
        else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsInt();
        }
        // Otherwise fallback
        return defaultValue;
    }

    */
/**
     * Reads an integer from a JSON element, allowing "auto" as a special case.
     *
     * @param element       The JSON element (e.g., json.get("x"))
     * @param defaultValue  The fallback if it's missing or invalid
     * @return an integer or -1 if "auto"
     *//*

    private static int readIntOrAuto(JsonElement element, int defaultValue) {
        if (element == null || element.isJsonNull()) {
            // Missing
            return defaultValue;
        }

        if (element.isJsonPrimitive()) {
            var primitive = element.getAsJsonPrimitive();
            // If it's a string
            if (primitive.isString()) {
                String strVal = primitive.getAsString();
                if (strVal.equalsIgnoreCase("auto")) {
                    return -1;
                } else {
                    try {
                        return Integer.parseInt(strVal);
                    } catch (NumberFormatException e) {
                        // Not a valid integer
                        return defaultValue;
                    }
                }
            }
            // If it's a number
            else if (primitive.isNumber()) {
                return primitive.getAsInt();
            }
        }
        return defaultValue;
    }
}*/
