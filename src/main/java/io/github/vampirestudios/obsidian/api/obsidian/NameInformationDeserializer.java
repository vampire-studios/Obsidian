package io.github.vampirestudios.obsidian.api.obsidian;

import com.google.gson.*;
import net.minecraft.resources.Identifier;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Flexible deserializer for {@link NameInformation} that supports three JSON forms:
 *
 * <h3>1. Inline string (English only, no color)</h3>
 * <pre>{@code "name": "Arm Cannon" }</pre>
 * Sets {@code text} directly.
 *
 * <h3>2. Shorthand translations string (English only, with other fields)</h3>
 * <pre>{@code
 * "name": {
 *     "translations": "Arm Cannon",
 *     "color": "#1EFF00"
 * }
 * }</pre>
 * Expands {@code translations} to {@code {"en_us": "Arm Cannon"}}.
 *
 * <h3>3. Full object (multiple languages)</h3>
 * <pre>{@code
 * "name": {
 *     "translations": { "en_us": "Arm Cannon", "de_de": "Armkanone" },
 *     "color": "#1EFF00"
 * }
 * }</pre>
 */
public class NameInformationDeserializer implements JsonDeserializer<NameInformation> {

    @Override
    public NameInformation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        // Form 1: plain string → literal text, no translations map
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            NameInformation info = new NameInformation();
            info.text = json.getAsString();
            return info;
        }

        if (!json.isJsonObject()) return new NameInformation();

        JsonObject obj = json.getAsJsonObject();
        NameInformation info = new NameInformation();

        if (obj.has("text") && obj.get("text").isJsonPrimitive()) {
            info.text = obj.get("text").getAsString();
        }

        if (obj.has("type") && obj.get("type").isJsonPrimitive()) {
            info.textType = obj.get("type").getAsString();
        }

        if (obj.has("id") && obj.get("id").isJsonPrimitive()) {
            info.id = Identifier.tryParse(obj.get("id").getAsString());
        }

        // translations: accept either a string shorthand (en_us) or a full map
        if (obj.has("translations")) {
            JsonElement translationsEl = obj.get("translations");

            if (translationsEl.isJsonPrimitive() && translationsEl.getAsJsonPrimitive().isString()) {
                // Form 2: shorthand — wrap the string as en_us
                Map<String, String> map = new HashMap<>();
                map.put("en_us", translationsEl.getAsString());
                info.translations = map;
            } else if (translationsEl.isJsonObject()) {
                // Form 3: full translations map
                Map<String, String> map = new HashMap<>();
                for (Map.Entry<String, JsonElement> entry : translationsEl.getAsJsonObject().entrySet()) {
                    if (entry.getValue().isJsonPrimitive()) {
                        map.put(entry.getKey(), entry.getValue().getAsString());
                    }
                }
                info.translations = map;
            }
        }

        return info;
    }
}
