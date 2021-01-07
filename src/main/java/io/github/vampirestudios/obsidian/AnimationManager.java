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

public class AnimationManager implements SimpleSynchronousResourceReloadListener, AutoCloseable {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final Identifier MISSING_IDENTIFIER = Obsidian.id("");
    private static final Identifier LISTENER_ID = Obsidian.id("animation");
    private final Map<Identifier, AnimationData> animData = new HashMap<>();
    private final ResourceManager resourceContainer;

    public AnimationManager(ResourceManager resourceManager) {
        this.resourceContainer = resourceManager;
    }

    @Override
    public Identifier getFabricId() { return LISTENER_ID; }

    public AnimationData getAnimationData(Identifier id) {
        return animData.computeIfAbsent(id, this::loadAnimationData);
    }

    private AnimationData loadAnimationData(Identifier identifier) {
        try {
            return AnimationData.load(this.resourceContainer, identifier);
        } catch (JsonParseException ex) {
            if (identifier != MISSING_IDENTIFIER)
                LOGGER.warn("Failed to load Animation Data[{}]: {}", identifier, ex);
            return getAnimationData(MISSING_IDENTIFIER);
        } catch (Throwable ex) {
            CrashReport crashReport = CrashReport.create(ex, "Registering animation");
            CrashReportSection crashReportSection = crashReport.addElement("Resource location being registered");
            crashReportSection.add("Resource location", identifier);
            crashReportSection.add("Animation Data", animData);
            throw new CrashException(crashReport);
        }
    }

    @Override
    public void apply(ResourceManager manager) {
        Lists.newArrayList(this.animData.keySet()).forEach(k -> animData.replace(k, loadAnimationData(k)));
        animData.put(MISSING_IDENTIFIER, ClientInit.GSON_CLIENT.fromJson(MISSING_ANIMATION_DATA, AnimationData.class));
    }

    @Override
    public void close() throws Exception {
        this.animData.clear();
    }

    private static final String MISSING_ANIMATION_DATA =
            "{\n" +
            "\t\"format_version\": \"1.8.0\",\n" +
            "\t\"animations\": {\n" +
            "\t\t\"animation.archiesarmy.fuckup\": {\n" +
            "\t\t\t\"animation_length\": 0.0,\n" +
            "\t\t\t\"override_previous_animation\": true," +
            "\t\t\t\"bones\": { }\n" +
            "\t\t}\n" +
            "\t}\n" +
            "}";
}