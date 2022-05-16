package io.github.vampirestudios.obsidian.client.resource;

import net.minecraft.resource.pack.ResourcePackProfile;

public class SPResourcePackProfile extends ResourcePackProfile {

    public SPResourcePackProfile(ResourcePackProfile copy) {
        super(copy.getName(), copy.isAlwaysEnabled(), copy::createResourcePack, copy.getDisplayName(), copy.getDescription(), copy.getCompatibility(), copy.getInitialPosition(), copy.isPinned(), copy.getSource());
    }
}