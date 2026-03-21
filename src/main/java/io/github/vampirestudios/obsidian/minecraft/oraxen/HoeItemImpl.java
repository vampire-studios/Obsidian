package io.github.vampirestudios.obsidian.minecraft.oraxen;

import eu.pb4.placeholders.api.parsers.TagParser;
import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class HoeItemImpl extends HoeItem {

    public NexoItem item;

    public HoeItemImpl(NexoItem item, ToolMaterial material, float attackDamage, float attackSpeed, Properties settings) {
        super(material, attackDamage, attackSpeed, settings);
        this.item = item;
    }

    @Override
    public Component getName(ItemStack stack) {
        return this.item.getName(this);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
        if (item.lore != null) {
            for (String lore : item.lore) {
                consumer.accept(TagParser.QUICK_TEXT_WITH_STF.parseNode(lore).toComponent());
            }
        }
    }
}