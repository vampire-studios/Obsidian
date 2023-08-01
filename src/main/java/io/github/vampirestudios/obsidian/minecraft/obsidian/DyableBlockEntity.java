package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import io.github.vampirestudios.obsidian.utils.ColorUtil;
import io.github.vampirestudios.obsidian.utils.MathHelper;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class DyableBlockEntity extends BlockEntity {

    public int dyeColor;

    public DyableBlockEntity(Block block, BlockPos pos, BlockState state) {
        super(BuiltInRegistries.BLOCK_ENTITY_TYPE.get(Utils.appendToPath(block.information.name.id, "_be")), pos, state);
        this.dyeColor = 0xFFFFFFFF;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.setDyeColor(nbt.getInt("color"));
        if (this.hasLevel() && this.level.isClientSide) this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(),
                net.minecraft.world.level.block.Block.UPDATE_ALL);
    }

    @Override
    public void saveAdditional(CompoundTag nbt) {
        nbt.putInt("color", this.getDyeColor());
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::saveWithoutMetadata);
    }

    public int getDyeColor() {
        return this.dyeColor;
    }

    public void setColorAndSync(int color) {
        setDyeColor(color);
    }

    public void setDyeColor(int dyeColor) {
        this.dyeColor = dyeColor;
    }

    public int getNewDyeColor(int color1In, int color2In) {
        int[] color1 = ColorUtil.toIntArray(color2In);
        int[] color2 = ColorUtil.toIntArray(color1In);
        double delta = Minecraft.getInstance().getFrameTime();
        int r = MathHelper.floor(net.minecraft.util.Mth.lerp(delta, color1[0], color2[0]));
        int g = MathHelper.floor(net.minecraft.util.Mth.lerp(delta, color1[1], color2[1]));
        int b = MathHelper.floor(net.minecraft.util.Mth.lerp(delta, color1[2], color2[2]));
        return ColorUtil.color(r, g, b);
    }

}