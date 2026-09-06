package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.EventActionHandler;
import io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/**
 * An addon fishing rod. Casting, reeling and the bobber are vanilla's; this adds the addon's lore,
 * glint and events, the same way {@link BrushItemImpl} does for brushes.
 *
 * <p>Lure and Luck of the Sea still work, since vanilla reads them off the stack.</p>
 */
public class FishingRodItemImpl extends FishingRodItem {

	public ToolItem item;

	public FishingRodItemImpl(ToolItem item, Properties settings) {
		super(settings);
		this.item = item;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return item.information.getItemSettings().hasEnchantmentGlint.orElse(stack.isEnchanted());
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay,
	                            Consumer<Component> tooltip, TooltipFlag context) {
		item.addLore(tooltip);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		EventActionHandler.handleOnUse(player, item);
		return super.use(level, player, hand);
	}

	@Override
	public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (!(attacker instanceof Player player)) return;
		EventActionHandler.handleHurtEnemy(target, player, item);
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
		if (!(entity instanceof Player player)) {
			super.inventoryTick(stack, level, entity, slot);
			return;
		}
		EventActionHandler.handleOnInventoryTick(player, item);
		super.inventoryTick(stack, level, entity, slot);
	}

	@Override
	public void onCraftedBy(ItemStack stack, Player player) {
		EventActionHandler.handleOnItemCrafted(player, item);
		super.onCraftedBy(stack, player);
	}
}
