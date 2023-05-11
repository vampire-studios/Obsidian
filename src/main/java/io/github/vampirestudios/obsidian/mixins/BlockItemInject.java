package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.minecraft.obsidian.CustomDyeableItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemInject {
    @Inject(
        cancellable = true,
        method = "updateCustomBlockEntityTag(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)Z",
        at = @At("HEAD")
    )
    private static void injected(Level world, Player player, BlockPos pos, ItemStack stack, CallbackInfoReturnable<Boolean> info) {
        if (stack.getItem() instanceof CustomDyeableItem) {
            MinecraftServer minecraftServer = world.getServer();
            if (minecraftServer != null) {
                CompoundTag compoundTag = stack.getTagElement("display");
                if (compoundTag != null) {
                    BlockEntity blockEntity = world.getBlockEntity(pos);
                    if (blockEntity != null) {
                        if (!world.isClientSide && blockEntity.onlyOpCanSetNbt() && (player == null || !player.canUseGameMasterBlocks())) {
                            info.setReturnValue(false);
                        }

                        CompoundTag compoundTag2 = blockEntity.saveWithoutMetadata();
                        CompoundTag compoundTag3 = compoundTag2.copy();
                        compoundTag2.merge(compoundTag);
                        if (!compoundTag2.contains("color") || compoundTag2.getInt("color") == 0) {
                            compoundTag2.putInt("color", 16777215);
                        }

                        compoundTag2.putInt("x", pos.getX());
                        compoundTag2.putInt("y", pos.getY());
                        compoundTag2.putInt("z", pos.getZ());
                        if (!compoundTag2.equals(compoundTag3)) {
                            blockEntity.load(compoundTag2);
                            blockEntity.setChanged();
                            info.setReturnValue(true);
                        }
                    }
                }

            }
            info.setReturnValue(false);
        }

    }
}
