package io.github.vampirestudios.obsidian.minecraft.obsidian;

import com.github.crimsondawn45.fabricshieldlib.lib.object.FabricShieldItem;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import io.github.vampirestudios.obsidian.api.obsidian.item.ShieldItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShieldItemImpl extends FabricShieldItem {

    public ShieldItem shieldItem;

    public ShieldItemImpl(ShieldItem shieldItem, Settings settings) {
        super(settings, shieldItem.cooldownTicks, shieldItem.information.enchantability, Registry.ITEM.get(shieldItem.repairItem));
        this.shieldItem = shieldItem;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return shieldItem.information.has_glint;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return shieldItem.information.is_enchantable;
    }

    @Override
    public Text getName() {
        return shieldItem.information.name.getName("item");
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (shieldItem.display != null && shieldItem.display.lore.length != 0) {
            for (TooltipInformation tooltipInformation : shieldItem.display.lore) {
                tooltip.add(tooltipInformation.getTextType("tooltip"));
            }
        }
    }
}
