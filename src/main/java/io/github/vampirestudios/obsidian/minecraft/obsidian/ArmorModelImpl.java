package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.ArmorModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;

public class ArmorModelImpl<T extends LivingEntity> extends BipedEntityModel<T> {

    public ArmorModelImpl(ArmorModel entityModelIn) {
        super(entityModelIn.getTexturedModelData().createModel());
    }

}
