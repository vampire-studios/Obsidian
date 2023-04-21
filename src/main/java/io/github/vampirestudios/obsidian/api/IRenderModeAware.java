package io.github.vampirestudios.obsidian.api;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public interface IRenderModeAware {
  default BakedModel getModel(ItemStack stack, ItemDisplayContext mode, BakedModel original) {
    return original;
  }
}