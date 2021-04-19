package io.github.vampirestudios.obsidian.api.obsidian.enchantments;

import io.github.vampirestudios.obsidian.AbstractExtendedEnum;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.function.Predicate;

public class EnchantmentTargetImpl extends AbstractExtendedEnum<EnchantmentTarget> implements EnchantmentTargetExtended {

    private final Identifier id;
    private final Predicate<Item> sound;

    public EnchantmentTargetImpl(Identifier id, Predicate<Item> sound) {
        super(null);
        this.id = id;
        this.sound = sound;
    }

    public EnchantmentTargetImpl(EnchantmentTarget vanilla) {
        super(vanilla);
        this.id = new Identifier(vanilla.name().toLowerCase());
        this.sound = vanilla::isAcceptableItem;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public Predicate<Item> isItemAcceptable() {
        return sound;
    }
}