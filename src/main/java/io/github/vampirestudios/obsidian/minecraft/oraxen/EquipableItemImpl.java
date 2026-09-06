package io.github.vampirestudios.obsidian.minecraft.oraxen;

import eu.pb4.placeholders.api.parsers.TagParser;
import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class EquipableItemImpl extends ItemImpl {

	public EquipableItemImpl(NexoItem item, Properties settings) {
		super(item, settings);
	}

	@Override
	public Component getName(ItemStack stack) {
		return this.item.getName(this);
	}

	@Override
	public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> consumer, TooltipFlag tooltipFlag) {
		if (item.lore != null) {
			for (String lore : item.lore) {
				consumer.accept(TagParser.QUICK_TEXT.parseNode(lore).toComponent());
			}
		}
	}

	@Override
	public void inventoryTick(ItemStack itemStack, ServerLevel serverLevel, Entity entity, @Nullable EquipmentSlot equipmentSlot) {
		if (item.mechanics != null && item.mechanics.cognitive_enhancement != null) {
			if (!(entity instanceof Player player)) return; // Only apply to players
			item.mechanics.cognitive_enhancement.onTick(player);
		}
		super.inventoryTick(itemStack, serverLevel, entity, equipmentSlot);
	}
}
