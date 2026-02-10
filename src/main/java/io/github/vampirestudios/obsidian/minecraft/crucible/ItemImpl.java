package io.github.vampirestudios.obsidian.minecraft.crucible;

import eu.pb4.placeholders.api.parsers.TagParser;
import io.github.vampirestudios.obsidian.api.crucible.CrucibleItem;
import io.github.vampirestudios.obsidian.api.crucible.SkillManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class ItemImpl extends Item {
    public final CrucibleItem item;

    public ItemImpl(CrucibleItem item, Properties settings) {
        super(settings);
        this.item = item;
    }

    @Override
    public Component getName(ItemStack stack) {
        return TagParser.QUICK_TEXT_WITH_STF.parseNode(item.Display).toText();
    }

    @Override
    public void appendHoverText(ItemStack stack,
                                TooltipContext tooltipContext,
                                TooltipDisplay tooltipDisplay,
                                Consumer<Component> tooltip,
                                TooltipFlag context) {
        if (item.Lore != null) {
            for (String lore : item.Lore) {
                tooltip.accept(TagParser.QUICK_TEXT_WITH_STF.parseNode(lore).toText());
            }
        }
    }
}