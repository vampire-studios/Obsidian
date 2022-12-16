package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(PointOfInterestTypes.class)
public interface PointOfInterestTypesAccessor {
	@Invoker
	static PointOfInterestType callRegister(
			Registry<PointOfInterestType> registry, RegistryKey<PointOfInterestType> key, Set<BlockState> states, int ticketCount, int searchDistance
	) {
		throw new UnsupportedOperationException();
	}
}
