package io.github.vampirestudios.obsidian.api;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.item.ItemStack;

public interface IRenderModeAware {
  default BakedModel getModel(ItemStack stack, ModelTransformationMode mode, BakedModel original) {
    return original;
  }
}