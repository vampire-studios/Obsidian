package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.villager.AddonVillagerTypes;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerType.class)
public class VillagerTypeMixin {

	@Inject(method = "byBiome", at = @At("HEAD"), cancellable = true)
	private static void obsidian$addonVillagerTypes(Holder<Biome> biome, CallbackInfoReturnable<ResourceKey<VillagerType>> cir) {
		ResourceKey<VillagerType> type = AddonVillagerTypes.byBiome(biome);
		if (type != null) {
			cir.setReturnValue(type);
		}
	}
}
