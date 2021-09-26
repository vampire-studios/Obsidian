package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.block.CraftingTableBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CraftingScreenHandler.class)
public class CraftingScreenHandlerMixin {

    @Shadow @Final private ScreenHandlerContext context;

    /**
     * @author Olivia
     */
    @Overwrite
    public boolean canUse(PlayerEntity player) {
        return this.context.get((world, pos) -> {
            return world.getBlockState(pos).getBlock() instanceof CraftingTableBlock;
        }, true);
    }
}
