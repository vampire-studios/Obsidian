package io.github.vampirestudios.obsidian.minecraft;

import io.github.vampirestudios.obsidian.api.TooltipInformation;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.text.TranslatableText;
import net.minecraft.world.World;

import java.util.List;

public class ItemImpl extends Item {

    public io.github.vampirestudios.obsidian.api.item.Item item;

    public ItemImpl(io.github.vampirestudios.obsidian.api.item.Item item, Settings settings) {
        super(settings);
        this.item = item;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return item.information.enchanted;
    }

    @Override
    public String getTranslationKey() {
        TranslatableText name = new TranslatableText(String.format("item.%s.%s", item.information.name.getNamespace(), item.information.name.getPath()));
        if (!item.information.name_color.isEmpty()) {
            String color = item.information.name_color.replace("#", "").replace("0x", "");
            name.setStyle(name.getStyle().withColor(new TextColor(Integer.parseInt(color, 16))));
        }
        return name.getString();
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if (item.display != null && item.display.lore.length != 0) {
            for (TooltipInformation tooltipInformation : item.display.lore) {
                tooltip.add(tooltipInformation.getTextType(tooltipInformation.text));
            }
        }
    }

}
