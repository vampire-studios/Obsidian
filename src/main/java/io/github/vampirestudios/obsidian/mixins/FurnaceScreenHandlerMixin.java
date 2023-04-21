package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.level.block.LoomBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FurnaceMenu.class)
public class FurnaceScreenHandlerMixin {

    @Shadow @Final private ContainerLevelAccess context;

    /**
     * @author Olivia
     */
    @Overwrite
    public boolean canUse(Player player) {
        return this.context.evaluate((world, pos) -> world.getBlockState(pos).getBlock() instanceof LoomBlock, true);
    }
}
