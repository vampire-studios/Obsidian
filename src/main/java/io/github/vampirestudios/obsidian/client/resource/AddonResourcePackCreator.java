package io.github.vampirestudios.obsidian.client.resource;

import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;

import java.util.Map;

public class AddonResourcePackCreator implements ResourcePackProvider {
   public AddonResourcePackCreator() {
   }

   @Override
   public <T extends ResourcePackProfile> void register(Map<String, T> registry, ResourcePackProfile.Factory<T> factory) {
      AddonResourcePack addonResourcePack = new AddonResourcePack();
      registry.put(addonResourcePack.getName(), ResourcePackProfile.of(addonResourcePack.getName(), true, () -> addonResourcePack, factory, ResourcePackProfile.InsertionPosition.BOTTOM));
   }
}