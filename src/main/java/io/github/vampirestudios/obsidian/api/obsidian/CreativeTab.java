package io.github.vampirestudios.obsidian.api.obsidian;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.Optional;

public class CreativeTab {
    public static final Codec<CreativeTab> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            NameInformation.CODEC.fieldOf("name").forGetter(creativeTab -> creativeTab.nameInformation),
            ResourceLocation.CODEC.optionalFieldOf("texture").forGetter(creativeTab -> creativeTab.texture),
            ResourceLocation.CODEC.fieldOf("icon").forGetter(creativeTab -> creativeTab.icon),
            RegistryCodecs.homogeneousList(Registries.ITEM).optionalFieldOf("items", HolderSet.empty()).forGetter(creativeTab -> creativeTab.items),
            Codec.BOOL.optionalFieldOf("no_scroll_bar", false).forGetter(creativeTab -> creativeTab.noScrollBar)
    ).apply(instance, CreativeTab::new));

    public NameInformation nameInformation;
    public Optional<ResourceLocation> texture;
    public ResourceLocation icon;
    public HolderSet<Item> items;
    public boolean noScrollBar;

    public CreativeTab(NameInformation nameInformation, Optional<ResourceLocation> texture, ResourceLocation icon, HolderSet<Item> items, boolean noScrollBar) {
        this.nameInformation = nameInformation;
        this.texture = texture;
        this.icon = icon;
        this.items = items;
        this.noScrollBar = noScrollBar;
    }
}
