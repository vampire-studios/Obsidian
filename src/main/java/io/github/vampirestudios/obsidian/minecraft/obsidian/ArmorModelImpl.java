package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.ArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;

public class ArmorModelImpl<T extends LivingEntity> extends HumanoidModel<T> {

    public ArmorModelImpl(ArmorModel entityModelIn) {
        super(entityModelIn.getTexturedModelData().bakeRoot());
    }

}
