package io.github.vampirestudios.obsidian.registry;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.block.entity.PaintingTableBlockEntity;
import io.github.vampirestudios.obsidian.block.entity.RotationBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class OBE {
	public static BlockEntityType<PaintingTableBlockEntity> PAINTING_TABLE;

	/** Shared by every rotation block; the blocks are added to it as they register. */
	public static BlockEntityType<RotationBlockEntity> ROTATION_BLOCK;

	public static void init() {
		PAINTING_TABLE = Obsidian.OBSIDIAN_REGISTRY_HELPER.registerBlockEntity(
				FabricBlockEntityTypeBuilder.create(PaintingTableBlockEntity::new),
				"painting_table"
		);

		ROTATION_BLOCK = Obsidian.OBSIDIAN_REGISTRY_HELPER.registerBlockEntity(
				FabricBlockEntityTypeBuilder.create(RotationBlockEntity::new),
				"rotation_block"
		);
	}
}
