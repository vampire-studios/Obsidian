package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.EntityModel;
import io.github.vampirestudios.obsidian.registry.Registries;
import net.minecraft.client.model.geom.ModelPart;

public class EntityModelImpl<T extends EntityImplRenderState> extends net.minecraft.client.model.EntityModel<T> {

	public EntityModel entityModel;

	public EntityModelImpl(EntityModel entityModelIn) {
		super(entityModelIn.getTexturedModelData().bakeRoot());
		this.entityModel = entityModelIn;
	}

	public EntityModelImpl(ModelPart part) {
		super(part);
		this.entityModel = null;
	}

	@Override
	public void setupAnim(EntityImplRenderState state) {
		if (Registries.ANIMATION_DEFINITIONS != null) {
			state.animationStates.forEach((animationState, identifier) -> {
				var animation = Registries.ANIMATION_DEFINITIONS.getValue(identifier);
				animation.bake(root).apply(animationState, state.ageInTicks);
			});
		}
	}

}
