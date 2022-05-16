package io.github.vampirestudios.obsidian.mixins.client;

import io.github.vampirestudios.obsidian.ExpandedModelPart;
import io.github.vampirestudios.obsidian.ExpandedSinglePartEntityModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(SinglePartEntityModel.class)
public abstract class SinglePartEntityModelMixin implements ExpandedSinglePartEntityModel {
	@Override
	public Optional<ModelPart> getAnyDescendantWithName(String string) {
		return this.getPart().traverse().filter(modelPart -> ((ExpandedModelPart)modelPart).hasChild(string)).findFirst().map(modelPart -> modelPart.getChild(string));
	}

	@Shadow public abstract ModelPart getPart();
}
