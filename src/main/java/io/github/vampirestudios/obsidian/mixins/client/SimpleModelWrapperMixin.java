package io.github.vampirestudios.obsidian.mixins.client;

import com.google.common.collect.Multimap;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(SimpleModelWrapper.class)
public class SimpleModelWrapperMixin {

	@Shadow
	public static @Nullable Multimap<Identifier, Identifier> findNonBlockSprites(QuadCollection geometry) {
		throw new UnsupportedOperationException("Implemented via mixin");
	}

	@Shadow
	@Final
	private static Logger LOGGER;

	/**
	 * @author a
	 * @reason a
	 */
	@Overwrite
	public static BlockStateModelPart bake(ModelBaker modelBakery, Identifier location, ModelState state) {
		ResolvedModel model = modelBakery.getModel(location);
		TextureSlots textureSlots = model.getTopTextureSlots();
		boolean hasAmbientOcclusion = model.getTopAmbientOcclusion();
		Material.Baked particleMaterial = model.resolveParticleMaterial(textureSlots, modelBakery);
		QuadCollection geometry = model.bakeTopGeometry(textureSlots, modelBakery, state);
		return new SimpleModelWrapper(geometry, hasAmbientOcclusion, particleMaterial);
	}
}
