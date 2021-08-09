package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class DyeableBlockEntity extends BlockEntity implements BlockEntityClientSerializable, RenderAttachmentBlockEntity {
    private int color = 16777215;

    public DyeableBlockEntity(Block block, BlockPos pos, BlockState state) {
        super(Registry.BLOCK_ENTITY_TYPE.get(Utils.appendToPath(block.information.name.id, "_be")), pos, state);
    }

    public int getColor() {
        return this.color;
    }

    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putInt("color", this.color);
        return tag;
    }

    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.setColor(tag.getInt("color"));
    }

    @Nullable
    public Object getRenderAttachmentData() {
        this.markDirty();
        return this;
    }

    public void fromClientTag(NbtCompound compoundTag) {
        this.readNbt(/*this.method_11010(), */compoundTag);
    }

    public NbtCompound toClientTag(NbtCompound compoundTag) {
        return this.writeNbt(compoundTag);
    }

    public void setColor(int color) {
        this.color = color;
        this.markDirty();
    }
}
