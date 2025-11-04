package io.github.vampirestudios.obsidian;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

import java.util.Locale;

public class OStructurePieceTypes {
	public static final StructurePieceType BACKROOMS_ROOM = setPieceId(BackroomsPieces.BackroomsRoom::new, "BSRoom");

	public static void init() {}

	private static StructurePieceType setFullContextPieceId(StructurePieceType type, String id) {
		return Registry.register(BuiltInRegistries.STRUCTURE_PIECE, Obsidian.id(id.toLowerCase(Locale.ROOT)), type);
	}

	private static StructurePieceType setPieceId(StructurePieceType.ContextlessType simplePieceType, String id) {
		return setFullContextPieceId(simplePieceType, id);
	}
}
