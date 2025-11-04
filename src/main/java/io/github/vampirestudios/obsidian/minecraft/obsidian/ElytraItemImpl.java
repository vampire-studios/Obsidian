package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.item.Item;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantable;

public class ElytraItemImpl extends net.minecraft.world.item.Item {

    private final Item item;

    public ElytraItemImpl(Item item, Properties settings) {
        super(settings.component(DataComponents.GLIDER, Unit.INSTANCE)
                .component(DataComponents.ENCHANTABLE, new Enchantable(item.information.getItemSettings().enchantability)));
        this.item = item;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return item.information.getItemSettings().hasEnchantmentGlint.orElse(stack.isEnchanted());
    }
}
