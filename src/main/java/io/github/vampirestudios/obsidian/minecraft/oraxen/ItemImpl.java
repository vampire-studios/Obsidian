package io.github.vampirestudios.obsidian.minecraft.oraxen;

import eu.pb4.placeholders.api.parsers.TagParser;
import io.github.vampirestudios.obsidian.api.nexo.NexoItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ItemImpl extends Item {

	public NexoItem item;

	public ItemImpl(NexoItem item, Properties settings) {
		super(settings);
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
				consumer.accept(TagParser.QUICK_TEXT.parseNode(lore).toComponent());
			}
		}
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
		if (item.mechanics != null && item.mechanics.cognitive_enhancement != null) {
			item.mechanics.cognitive_enhancement.enhanceCognition(player);
			player.getCooldowns().addCooldown(this.getDefaultInstance(), item.mechanics.cognitive_enhancement.duration * 20);
			this.getDefaultInstance().shrink(1);
			return InteractionResult.SUCCESS;
		}
		return super.use(level, player, usedHand);
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
