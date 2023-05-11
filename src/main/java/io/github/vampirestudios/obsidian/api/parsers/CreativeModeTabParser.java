package io.github.vampirestudios.obsidian.api.parsers;

import com.google.gson.JsonObject;
import io.github.vampirestudios.obsidian.api.builders.BaseBuilder;
import io.github.vampirestudios.obsidian.api.builders.CreativeModeTabBuilder;
import io.github.vampirestudios.obsidian.utils.parse.JParse;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

public class CreativeModeTabParser extends ThingParser<CreativeModeTabBuilder> {
    public static final Logger LOGGER = LogManager.getLogger();

    public CreativeModeTabParser() {
        super(GSON, "creative_mode_tab");
        registerTabs();
    }

    private void registerTabs() {

        processAndConsumeErrors(getThingType(), getBuilders(), thing -> {
            var tab = thing.get();
            var icon = tab.icon();
            var name = tab.name();
            CreativeModeTab creativeModeTab = FabricItemGroup.builder()
                    .icon(() -> icon.get().getDefaultInstance())
                    .title(Component.translatable(name))
                    .displayItems((a, b) -> {}).build();
            Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, thing.getRegistryName(), creativeModeTab);
        }, BaseBuilder::getRegistryName);
    }

    @Override
    protected void finishLoadingInternal() {
        getBuilders().forEach(CreativeModeTabBuilder::get);
    }

    @Override
    protected CreativeModeTabBuilder processThing(ResourceLocation key, JsonObject data, Consumer<CreativeModeTabBuilder> builderModification) {
        final CreativeModeTabBuilder builder = CreativeModeTabBuilder.begin(this, key);

        JParse.begin(data)
                .key("icon", val -> val.string().map(ResourceLocation::new).handle(builder::setIcon));

        builderModification.accept(builder);

        return builder;
    }
}