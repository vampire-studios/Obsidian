package io.github.vampirestudios.obsidian.mixins;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.EnumMap;
import java.util.function.Supplier;

@Mixin(ArmorMaterials.class)
public interface ArmorMaterialsAccessor {
    @Invoker("<init>")
    static ArmorMaterials createArmorMaterials(
            String string2, int j, EnumMap<ArmorItem.Type, Integer> enumMap, int k, SoundEvent soundEvent, float f, float g, Supplier<Ingredient> supplier
    ) {
        throw new UnsupportedOperationException();
    }
}
