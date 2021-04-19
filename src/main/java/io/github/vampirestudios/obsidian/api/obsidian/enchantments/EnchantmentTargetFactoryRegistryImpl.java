package io.github.vampirestudios.obsidian.api.obsidian.enchantments;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.enchantment.EnchantmentTarget;

import java.util.Optional;

public class EnchantmentTargetFactoryRegistryImpl implements EnchantmentTargetFactoryRegistry {

    private static final EnchantmentTargetExtended DEFAULT = EnchantmentTargetExtended.VANILLA.get(EnchantmentTarget.ARMOR);
    private final ReferenceArrayList<EnchantmentTargetFactory> factories = new ReferenceArrayList<>();

    public void register(EnchantmentTargetFactory factory) {
        factories.add(factory);
    }

    @Override
    public Optional<EnchantmentTargetExtended> get(String name) {
        // functional pogramming go brrrrrrrrrrrr                           - comments to maintain my sanity
        return factories.stream()                                           // get all the factories
                .map(factory -> factory.get(name))             // lazily evaluate/get them in order
                .filter(Optional::isPresent)                                // filter out valid instruments
                .findFirst()                                                // find the first valid one
                .orElseGet(                                                 // if no dice then use the vanilla fallback
                    () -> EnchantmentTargetFactory.VANILLA.get(name)  // (which may or may not exist)
                );
    }

    @Override
    public EnchantmentTargetExtended getOrDefault(String name) {
        return this.get(name).orElse(DEFAULT);
    }
}