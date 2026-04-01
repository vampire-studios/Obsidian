package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.EventActionHandler;
import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class SpearItemImpl extends Item {

    public WeaponItem item;

    public SpearItemImpl(WeaponItem item, ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Properties settings) {
        super(buildProps(item, toolMaterial, settings));
        this.item = item;
    }

    @SuppressWarnings("unchecked")
    private static <T> Properties buildProps(WeaponItem item, ToolMaterial toolMaterial, Properties settings) {
        settings.spear(
                toolMaterial,
                item.spear.charge_time,         // attackDuration
                item.spear.damage_multiplier,   // damageMultiplier
                item.spear.delay,               // delay
                item.spear.dismount_time,       // dismountTime
                item.spear.dismount_threshold,  // dismountThreshold
                item.spear.knockback_time,      // knockbackTime
                item.spear.knockback_threshold, // knockbackThreshold
                item.spear.damage_time,         // damageTime
                item.spear.damage_threshold     // damageThreshold
        );
        if (item.components != null) {
            for (TypedDataComponent<?> entry : item.components) {
                applyTyped(settings, entry);
            }
        }
        return settings;
    }

    @SuppressWarnings("unchecked")
    private static <T> void applyTyped(Item.Properties props, TypedDataComponent<T> entry) {
        props.component(entry.type(), entry.value());
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return item.information.getItemSettings().hasEnchantmentGlint.orElse(stack.isEnchanted());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag context) {
        item.addLore(tooltip);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!(attacker instanceof Player player)) return;
        EventActionHandler.handleHurtEnemy(target, player, item);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (!(livingEntity instanceof Player player)) {
            super.onUseTick(level, livingEntity, stack, remainingUseDuration);
            return;
        }
        EventActionHandler.handleOnUseTick(player, item);
        super.onUseTick(level, livingEntity, stack, remainingUseDuration);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        EventActionHandler.handleOnUseOn(context, item);
        return super.useOn(context);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (!(livingEntity instanceof Player player)) return super.finishUsingItem(stack, level, livingEntity);
        EventActionHandler.handleOnFinishUsing(player, item);
        return super.finishUsingItem(stack, level, livingEntity);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel serverLevel, Entity entity, @Nullable EquipmentSlot equipmentSlot) {
        if (!(entity instanceof Player player)) {
            super.inventoryTick(itemStack, serverLevel, entity, equipmentSlot);
            return;
        }
        EventActionHandler.handleOnInventoryTick(player, item);
        super.inventoryTick(itemStack, serverLevel, entity, equipmentSlot);
    }

    @Override
    public void onCraftedBy(ItemStack itemStack, Player player) {
        EventActionHandler.handleOnItemCrafted(player, item);
        super.onCraftedBy(itemStack, player);
    }
}
