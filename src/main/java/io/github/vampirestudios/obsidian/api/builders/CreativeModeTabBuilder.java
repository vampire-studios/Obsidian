package io.github.vampirestudios.obsidian.api.builders;

import io.github.vampirestudios.obsidian.api.FlexCreativeModeTab;
import io.github.vampirestudios.obsidian.api.Utils;
import io.github.vampirestudios.obsidian.api.parsers.ThingParser;
import net.minecraft.resources.ResourceLocation;

public class CreativeModeTabBuilder extends BaseBuilder<FlexCreativeModeTab, CreativeModeTabBuilder> {
    public static CreativeModeTabBuilder begin(ThingParser<CreativeModeTabBuilder> ownerParser, ResourceLocation registryName) {
        return new CreativeModeTabBuilder(ownerParser, registryName);
    }

    private ResourceLocation iconItem;

    private CreativeModeTabBuilder(ThingParser<CreativeModeTabBuilder> ownerParser, ResourceLocation registryName) {
        super(ownerParser, registryName);
    }

    @Override
    protected String getThingTypeDisplayName() {
        return "Creative Mode Tab";
    }

    public void setIcon(ResourceLocation iconItem) {
        this.iconItem = iconItem;
    }

    @Override
    protected FlexCreativeModeTab buildInternal() {
        ResourceLocation registryName = getRegistryName();
        return new FlexCreativeModeTab(registryName.getNamespace() + "." + registryName.getPath().replace("/", "."), () -> Utils.getItemOrCrash(iconItem));
    }
}