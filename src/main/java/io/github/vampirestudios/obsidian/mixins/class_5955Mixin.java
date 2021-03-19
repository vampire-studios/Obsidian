package io.github.vampirestudios.obsidian.mixins;

import io.github.vampirestudios.obsidian.ConvertableOxidizableBlock;
import io.github.vampirestudios.obsidian.Obsidian;
import net.minecraft.block.Block;
import net.minecraft.class_5955;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Objects;
import java.util.Optional;

@Mixin(class_5955.class)
public interface class_5955Mixin {

    /**
     * @author Olivia
     */
    @Overwrite
    static Optional<Block> method_34737(Block block) {
        return Optional.ofNullable(Objects.requireNonNull(Obsidian.CONVERTABLE_OXIDIZABLE_BLOCKS.get(Registry.BLOCK.getId(block))).right);
    }

    /**
     * @author Olivia, CatCore
     */
    @Overwrite
    static Optional<Block> method_34732(Block block) {
        Block block1 = null;

        for (ConvertableOxidizableBlock convertableOxidizableBlock : Obsidian.CONVERTABLE_OXIDIZABLE_BLOCKS) {
            if (convertableOxidizableBlock.right == block) {
                block1 = convertableOxidizableBlock.left;
                break;
            }
        }

        return Optional.ofNullable(block1);
    }

}