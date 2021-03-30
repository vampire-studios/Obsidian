package io.github.vampirestudios.obsidian;

import com.google.gson.JsonObject;
import dev.inkwell.conrad.api.Config;
import dev.inkwell.conrad.api.value.ValueKey;
import dev.inkwell.conrad.api.value.data.SaveType;
import dev.inkwell.conrad.api.value.serialization.ConfigSerializer;
import dev.inkwell.conrad.api.value.serialization.GsonSerializer;
import org.jetbrains.annotations.NotNull;

public class ModConfig extends Config<JsonObject> {
    public static final ValueKey<String> addon_folder = value("obsidian_addons");

    @Override
    public @NotNull String getName() {
        return "config";
    }

    @Override
    public @NotNull ConfigSerializer<JsonObject> getSerializer() {
        return GsonSerializer.DEFAULT;
    }

    @Override
    public @NotNull SaveType getSaveType() {
        return SaveType.USER;
    }
}