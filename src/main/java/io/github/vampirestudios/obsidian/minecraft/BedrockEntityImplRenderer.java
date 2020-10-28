package io.github.vampirestudios.obsidian.minecraft;

import io.github.vampirestudios.obsidian.api.entity.Entity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import software.bernie.geckolib.renderer.geo.GeoEntityRenderer;

public class BedrockEntityImplRenderer extends GeoEntityRenderer<EntityImpl> {

    public BedrockEntityImplRenderer(EntityRenderDispatcher entityRenderDispatcher, Entity entity) {
        super(entityRenderDispatcher, new GeckoEntityModel<>(entity));
    }

}
