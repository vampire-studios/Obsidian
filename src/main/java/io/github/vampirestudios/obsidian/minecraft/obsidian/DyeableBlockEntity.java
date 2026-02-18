package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.utils.ColorUtil;
import io.github.vampirestudios.obsidian.utils.MathHelper;
import io.github.vampirestudios.obsidian.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class DyeableBlockEntity extends BlockEntity {

    private int dyeColor;

    public DyeableBlockEntity(Identifier id, BlockPos pos, BlockState state) {
        super(BuiltInRegistries.BLOCK_ENTITY_TYPE.getValue(Utils.appendToPath(id, "_be")), pos, state);
        this.dyeColor = 0xFFFFFFFF;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.setDyeColor(input.getIntOr("color", -1));
        if (this.hasLevel() && this.level.isClientSide()) this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(),
                net.minecraft.world.level.block.Block.UPDATE_CLIENTS);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        output.putInt("color", this.getDyeColor());
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this, BlockEntity::saveWithoutMetadata);
    }

    public int getDyeColor() {
        return this.dyeColor;
    }

    public void setDyeColor(int color) {
        if (this.dyeColor == color) return;

        this.dyeColor = color;
        this.setChanged();

        if (this.level != null) {
            this.level.sendBlockUpdated(
                    this.worldPosition,
                    this.getBlockState(),
                    this.getBlockState(),
                    net.minecraft.world.level.block.Block.UPDATE_CLIENTS
            );
        }
    }

    public int getNewDyeColor(int color1In, int color2In) {
        float t = Minecraft.getInstance().getFps(); // if available in your version
        t = net.minecraft.util.Mth.clamp(t, 0.0F, 1.0F);
        int[] color1 = ColorUtil.toIntArray(color1In);
        int[] color2 = ColorUtil.toIntArray(color2In);
        double delta = Minecraft.getInstance().getFrameTimeNs();
        int r = MathHelper.floor(net.minecraft.util.Mth.lerp(delta, color1[0], color2[0]));
        int g = MathHelper.floor(net.minecraft.util.Mth.lerp(delta, color1[1], color2[1]));
        int b = MathHelper.floor(net.minecraft.util.Mth.lerp(delta, color1[2], color2[2]));
        return ColorUtil.color(r, g, b);
    }

}