package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.block.LoomBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.FurnaceScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FurnaceScreenHandler.class)
public class FurnaceScreenHandlerMixin {

    @Shadow @Final private ScreenHandlerContext context;

    /**
     * @author Olivia
     */
    @Overwrite
    public boolean canUse(PlayerEntity player) {
        return this.context.get((world, pos) -> world.getBlockState(pos).getBlock() instanceof LoomBlock, true);
    }
}
