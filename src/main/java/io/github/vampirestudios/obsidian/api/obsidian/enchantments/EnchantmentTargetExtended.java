package io.github.vampirestudios.obsidian.api.obsidian.enchantments;

import io.github.vampirestudios.obsidian.CachedVanillaEnumFactory;
import io.github.vampirestudios.obsidian.api.ExtendedEnum;
import io.github.vampirestudios.obsidian.api.VanillaEnumFactory;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;

import java.util.function.Predicate;

/**
 * A custom instrument for note blocks.
 *
 * @see net.minecraft.block.enums.Instrument
 * @see EnchantmentTargetFactoryRegistry
 * @author leocth
 * @since 0.2.0
 */
public interface EnchantmentTargetExtended extends ExtendedEnum<EnchantmentTarget>, StringIdentifiable {

    VanillaEnumFactory<EnchantmentTarget, EnchantmentTargetExtended> VANILLA
            = new CachedVanillaEnumFactory<EnchantmentTarget, EnchantmentTargetExtended>(EnchantmentTargetImpl::new);

    Identifier getId();

    Predicate<Item> isItemAcceptable();

    @Override
    default String asString() {
        return getId().toString();
    }

    static EnchantmentTargetExtended create(Identifier id, Predicate<Item> predicate) {
        return new EnchantmentTargetImpl(id, predicate);
    }
}