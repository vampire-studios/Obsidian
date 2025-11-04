package io.github.vampirestudios.obsidian.minecraft.oraxen;

import eu.pb4.placeholders.api.parsers.TagParser;
import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

/**
 * This is the default implementation for FabricCrossbow, allowing for the easy creation of new bows with no new modded functionality.
 */
public class CrossbowItemImpl extends CrossbowItem {
	public NexoItem item;

	public CrossbowItemImpl(NexoItem item, Properties settings) {
		super(settings);
		this.item = item;
	}

//	@Override
//	public boolean canBeDepleted() {
//		return item.Mechanics.durability != null && item.Mechanics.durability.value != -1;
//	}

	@Override
	public Component getName(ItemStack stack) {
		return this.item.getName(this);
	}

	@Override
	public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
		if (item.lore != null) {
			for (String lore : item.lore) {
				consumer.accept(TagParser.QUICK_TEXT_WITH_STF.parseNode(lore).toText());
			}
		}
	}
}