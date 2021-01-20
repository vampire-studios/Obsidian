package io.github.vampirestudios.obsidian;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import io.github.vampirestudios.obsidian.client.ClientInit;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class GeometryManager implements SimpleSynchronousResourceReloadListener, AutoCloseable{

    private static final Logger LOGGER = LogManager.getLogger();
    public static final Identifier MISSING_IDENTIFIER = Obsidian.id("");
    private static final Identifier LISTENER_ID = Obsidian.id("geometry");
    private final Map<Identifier, GeometryData> modelData = new HashMap<>();
    private final ResourceManager resourceContainer;

    public GeometryManager(ResourceManager resourceManager) {
        this.resourceContainer = resourceManager;
    }

    @Override
    public Identifier getFabricId() { return LISTENER_ID; }

    public GeometryData getModelData(Identifier id) {
        return modelData.computeIfAbsent(id, this::loadGeometryData);
    }

    private GeometryData loadGeometryData(Identifier identifier) {
        try {
            return GeometryData.load(this.resourceContainer, identifier);
        } catch (JsonParseException var6) {
            if (identifier != MISSING_IDENTIFIER)
                LOGGER.warn("Failed to load Geometry Data: {}", identifier, var6);
            return getModelData(MISSING_IDENTIFIER);
        } catch (Throwable var7) {
            CrashReport crashReport = CrashReport.create(var7, "Registering model");
            CrashReportSection crashReportSection = crashReport.addElement("Resource location being registered");
            crashReportSection.add("Resource location", identifier);
            crashReportSection.add("Modeldata", modelData);
            throw new CrashException(crashReport);
        }
    }

    @Override
    public void apply(ResourceManager manager) {
        Lists.newArrayList(this.modelData.keySet()).stream().filter(i -> !i.equals(MISSING_IDENTIFIER)).forEach(k -> modelData.replace(k, loadGeometryData(k)));
        modelData.put(MISSING_IDENTIFIER, ClientInit.GSON_CLIENT.fromJson(MISSING_MODEL_DATA, GeometryData.class));
    }

    @Override
    public void close() throws Exception {
        this.modelData.clear();
    }

    private static final String MISSING_MODEL_DATA =
            "{\n" +
            "\t\"format_version\": \"1.12.0\",\n" +
            "\t\"minecraft:geometry\": [\n" +
            "\t\t{\n" +
            "\t\t\t\"description\": {\n" +
            "\t\t\t\t\"identifier\": \"geometry.obsidian.entities.fuckup\",\n" +
            "\t\t\t\t\"texture_width\": 16,\n" +
            "\t\t\t\t\"texture_height\": 16,\n" +
            "\t\t\t\t\"visible_bounds_width\": 2,\n" +
            "\t\t\t\t\"visible_bounds_height\": 1,\n" +
            "\t\t\t\t\"visible_bounds_offset\": [0, 0, 0]\n" +
            "\t\t\t},\n" +
            "\t\t\t\"bones\": [\n" +
            "\t\t\t\t{\n" +
            "\t\t\t\t\t\"name\": \"root\",\n" +
            "\t\t\t\t\t\"pivot\": [0, 8, 0],\n" +
            "\t\t\t\t\t\"cubes\": [\n" +
            "\t\t\t\t\t\t{\"origin\": [-7, 1, -7], \"size\": [14, 14, 14], \"uv\": [0, 0]}\n" +
            "\t\t\t\t\t]\n" +
            "\t\t\t\t}\n" +
            "\t\t\t]\n" +
            "\t\t}\n" +
            "\t]\n" +
            "}";
}