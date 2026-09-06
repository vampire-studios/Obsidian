package io.github.vampirestudios.obsidian;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;

public class OStructureTypes {
	public static final StructureType<BackroomsStructure> BACKROOMS = register("backrooms", BackroomsStructure.CODEC);

	public static void init() {
	}

	private static <S extends Structure> StructureType<S> register(String id, MapCodec<S> codec) {
		return Registry.register(BuiltInRegistries.STRUCTURE_TYPE, Obsidian.id(id), () -> codec);
	}
}
