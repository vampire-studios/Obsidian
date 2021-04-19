package io.github.vampirestudios.obsidian.api.obsidian.enchantments;

import net.minecraft.enchantment.EnchantmentTarget;

import java.util.Optional;

/**
 * Factory type that returns a {@link Instrument} with context.
 *
 * @author leocth
 * @since 0.2.0
 */
@FunctionalInterface
public interface EnchantmentTargetFactory {

    /**
     * A fallback {@link EnchantmentTargetFactory} that returns vanilla instruments based on the block beneath the note block.
     */
    EnchantmentTargetFactory VANILLA = (name) ->
            Optional.ofNullable(
                EnchantmentTargetImpl.VANILLA.get(
                    EnchantmentTarget.valueOf(
                        name
                    )
                )
            );

    Optional<EnchantmentTargetExtended> get(String name);
}