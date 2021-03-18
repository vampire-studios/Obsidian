package io.github.vampirestudios.obsidian.mixins;

import com.google.common.collect.BiMap;
import net.minecraft.block.Block;
import net.minecraft.class_5955;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Supplier;

@Mixin(class_5955.class)
public interface class_5955Accessor {
    @Accessor
    static Supplier<BiMap<Block, Block>> getField_29564() {
        throw new UnsupportedOperationException();
    }

    @Mutable
    @Accessor
    static void setField_29564(Supplier<BiMap<Block, Block>> field_29564) {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static Supplier<BiMap<Block, Block>> getField_29565() {
        throw new UnsupportedOperationException();
    }

    @Mutable
    @Accessor
    static void setField_29565(Supplier<BiMap<Block, Block>> field_29565) {
        throw new UnsupportedOperationException();
    }
}
