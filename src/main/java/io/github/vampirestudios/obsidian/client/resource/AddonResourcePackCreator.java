package io.github.vampirestudios.obsidian.client.resource;

import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;

import java.util.function.Consumer;

public class AddonResourcePackCreator implements ResourcePackProvider {

   @Override
   public void register(Consumer<ResourcePackProfile> consumer, ResourcePackProfile.Factory factory) {
      AddonResourcePack addonResourcePack = new AddonResourcePack();
      consumer.accept(ResourcePackProfile.of(addonResourcePack.getName(), true, () -> addonResourcePack, factory, ResourcePackProfile.InsertionPosition.BOTTOM, ResourcePackSource.method_29486("pack.source.obsidian")));
   }

}