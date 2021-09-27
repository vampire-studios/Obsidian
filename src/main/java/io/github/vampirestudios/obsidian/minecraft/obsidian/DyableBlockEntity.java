package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.utils.ColorUtil;
import io.github.vampirestudios.obsidian.utils.MHelper;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

public class DyableBlockEntity extends BlockEntity implements BlockEntityClientSerializable {

    public int dyeColor;

    public DyableBlockEntity(Block block) {
        super(Registry.BLOCK_ENTITY_TYPE.get(Utils.appendToPath(block.information.name.id, "_be")));
        this.dyeColor = 0xFFFFFFFF;
    }

    @Override
    public void fromTag(BlockState state, NbtCompound nbt) {
        super.fromTag(state, nbt);
        this.setDyeColor(nbt.getInt("dyeColor"));
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("dyeColor", this.getDyeColor());
        return nbt;
    }

    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 127, this.toInitialChunkDataNbt());
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.writeNbt(new NbtCompound());
    }

    public int getDyeColor() {
        return this.dyeColor;
    }

    public void setColorAndSync(int color) {
        setDyeColor(color);
        sync();
    }

    public void setDyeColor(int dyeColor) {
        this.dyeColor = dyeColor;
    }

    public int getNewDyeColor(int color1In, int color2In) {
        int[] color1 = ColorUtil.toIntArray(color2In);
        int[] color2 = ColorUtil.toIntArray(color1In);
        double delta = MinecraftClient.getInstance().getTickDelta();
        int r = MHelper.floor(MathHelper.lerp(delta, color1[0], color2[0]));
        int g = MHelper.floor(MathHelper.lerp(delta, color1[1], color2[1]));
        int b = MHelper.floor(MathHelper.lerp(delta, color1[2], color2[2]));
        return ColorUtil.color(r, g, b);
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        dyeColor = tag.getInt("dyeColor");
//        world.updateListeners(pos, getCachedState(), getCachedState(), net.minecraft.block.Block.);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        System.out.println("Test");
        tag.putInt("dyeColor", this.dyeColor);
        return tag;
    }

}