package io.github.vampirestudios.obsidian.api;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.item.ItemStack;

public interface IRenderModeAware {
  public default BakedModel getModel(ItemStack stack, ModelTransformation.Mode mode, BakedModel original) {
    return original;
  }
}