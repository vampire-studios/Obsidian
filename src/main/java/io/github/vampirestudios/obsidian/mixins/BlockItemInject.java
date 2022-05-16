package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.minecraft.obsidian.CustomDyeableItem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemInject {
    @Inject(
        cancellable = true,
        method = {"writeTagToBlockEntity"},
        at = @At("HEAD")
    )
    private static void injected(World world, PlayerEntity player, BlockPos pos, ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        if (stack.getItem() instanceof CustomDyeableItem) {
            MinecraftServer minecraftServer = world.getServer();
            if (minecraftServer != null) {
                NbtCompound compoundTag = stack.getSubNbt("display");
                if (compoundTag != null) {
                    BlockEntity blockEntity = world.getBlockEntity(pos);
                    if (blockEntity != null) {
                        if (!world.isClient && blockEntity.copyItemDataRequiresOperator() && (player == null || !player.isCreativeLevelTwoOp())) {
                            info.setReturnValue(false);
                        }

                        NbtCompound compoundTag2 = blockEntity.toNbt();
                        NbtCompound compoundTag3 = compoundTag2.copy();
                        compoundTag2.copyFrom(compoundTag);
                        if (!compoundTag2.contains("color") || compoundTag2.getInt("color") == 0) {
                            compoundTag2.putInt("color", 16777215);
                        }

                        compoundTag2.putInt("x", pos.getX());
                        compoundTag2.putInt("y", pos.getY());
                        compoundTag2.putInt("z", pos.getZ());
                        if (!compoundTag2.equals(compoundTag3)) {
                            blockEntity.readNbt(compoundTag2);
                            blockEntity.markDirty();
                            info.setReturnValue(true);
                        }
                    }
                }

            }
            info.setReturnValue(false);
        }

    }
}
