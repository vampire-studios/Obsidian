package io.github.vampirestudios.obsidian.minecraft.oraxen;

import eu.pb4.placeholders.api.parsers.TagParser;
import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

/** A Nexo item that retains vanilla trident charging and throwing behaviour. */
public class NexoTridentItemImpl extends TridentItem {
	private final NexoItem item;

	public NexoTridentItemImpl(NexoItem item, Properties properties) {
		super(properties);
		this.item = item;
	}

	@Override
	public Component getName(ItemStack stack) {
		return item.getName(this);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
	                            Consumer<Component> consumer, TooltipFlag flag) {
		if (item.lore != null) {
			for (String line : item.lore) consumer.accept(TagParser.QUICK_TEXT.parseNode(line).toComponent());
		}
	}
}
