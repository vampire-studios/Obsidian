package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class DyableBlockEntity extends BlockEntity {

    public int dyeColor;

    public DyableBlockEntity(Block block, BlockPos pos, BlockState state) {
        super(Registry.BLOCK_ENTITY_TYPE.get(Utils.appendToPath(block.information.name.id, "_be")), pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.dyeColor = nbt.getInt("dyeColor");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("dyeColor", this.dyeColor);
        return nbt;
    }

    public int getDyeColor() {
        return dyeColor;
    }

}