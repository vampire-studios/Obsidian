package io.github.vampirestudios.obsidian.animation;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.JsonOps;
import io.github.vampirestudios.obsidian.Obsidian;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;

public final class JsonEntityModelUtil {
    public static final Gson GSON = new Gson();

    private JsonEntityModelUtil() {}

    public static Optional<TexturedModelData> readJson(InputStream data) {
        JsonElement json = GSON.fromJson(GSON.newJsonReader(new InputStreamReader(data)), JsonObject.class);

        return Codecs.Model.TEXTURED_MODEL_DATA.decode(JsonOps.INSTANCE, json).result().map(Pair::getFirst);
    }

    public static void loadModels(ResourceManager manager, Map<EntityModelLayer, TexturedModelData> models) {
        EntityModelLayers.getLayers().forEach(layer -> {
            var modelLoc = new Identifier(layer.getId().getNamespace(), "models/entity/"+layer.getId().getPath()+"/"+layer.getName()+".json");

            var res = manager.method_14486(modelLoc);

            if (res.isPresent()) {
                try {
                    try (var in = res.get().open()) {
                        var data = JsonEntityModelUtil.readJson(in);
                        data.ifPresent(model -> models.put(layer, model));
                    }
                } catch (IOException e) {
                    Obsidian.LOGGER.error(e);
                }
            }
        });
    }
}