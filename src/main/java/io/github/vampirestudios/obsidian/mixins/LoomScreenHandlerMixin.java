package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.level.block.LoomBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LoomMenu.class)
public class LoomScreenHandlerMixin {

    @Shadow @Final private ContainerLevelAccess access;

    /**
     * @author Olivia
     * @reason idk
     */
    @Overwrite
    public boolean stillValid(Player player) {
        return this.access.evaluate((world, pos) -> world.getBlockState(pos).getBlock() instanceof LoomBlock, true);
    }
}
