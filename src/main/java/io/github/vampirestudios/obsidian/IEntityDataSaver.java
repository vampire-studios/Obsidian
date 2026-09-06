package io.github.vampirestudios.obsidian;

import net.minecraft.nbt.CompoundTag;

public interface IEntityDataSaver {
	CompoundTag getPersistentData();
}