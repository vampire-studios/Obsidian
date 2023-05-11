package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Pack.class)
public interface PackAccessor {
    @Accessor
    String getId();

    @Mutable
    @Accessor
    void setId(String id);

    @Accessor
    Pack.ResourcesSupplier getResources();

    @Mutable
    @Accessor
    void setResources(Pack.ResourcesSupplier resources);

    @Accessor
    Component getTitle();

    @Mutable
    @Accessor
    void setTitle(Component title);

    @Accessor
    Component getDescription();

    @Mutable
    @Accessor
    void setDescription(Component description);

    @Accessor
    PackCompatibility getCompatibility();

    @Mutable
    @Accessor
    void setCompatibility(PackCompatibility compatibility);

    @Accessor
    FeatureFlagSet getRequestedFeatures();

    @Mutable
    @Accessor
    void setRequestedFeatures(FeatureFlagSet requestedFeatures);

    @Accessor
    Pack.Position getDefaultPosition();

    @Mutable
    @Accessor
    void setDefaultPosition(Pack.Position defaultPosition);

    @Accessor
    boolean isRequired();

    @Mutable
    @Accessor
    void setRequired(boolean required);

    @Accessor
    boolean isFixedPosition();

    @Mutable
    @Accessor
    void setFixedPosition(boolean fixedPosition);

    @Accessor
    PackSource getPackSource();

    @Mutable
    @Accessor
    void setPackSource(PackSource packSource);
}
