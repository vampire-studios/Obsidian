package io.github.vampirestudios.obsidian.api.obsidian.enchantments;

import net.minecraft.enchantment.EnchantmentTarget;

import java.util.Optional;

/**
 * Registry for {@link EnchantmentTargetFactory}s.
 *
 * @author leocth
 * @since 0.2.0
 */
public interface EnchantmentTargetFactoryRegistry {

    EnchantmentTargetFactoryRegistry INSTANCE = new EnchantmentTargetFactoryRegistryImpl();

    /**
     * Registers a {@link EnchantmentTargetFactory}.
     */
    void register(EnchantmentTargetFactory factory);

    /**
     * Gets an optional {@link EnchantmentTarget} by iterating through the registered factories.
     */
    Optional<EnchantmentTargetExtended> get(String name);

    /**
     * Gets an {@link EnchantmentTarget} by iterating through the registered factories, then
     * returns a default value ({@code Instrument.HARP}) if no factories return a valid result.
     */
    EnchantmentTargetExtended getOrDefault(String name);
}