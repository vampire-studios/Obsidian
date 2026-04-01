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
import net.minecraft.world.entity.ai.attributes.Attributes;
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

/**
 * Fast, low-damage sword with armour penetration.
 * On hit: deals bonus armour-bypassing damage equal to (target's armour value × armor_pierce).
 */
public class RapierItemImpl extends Item {

    public final WeaponItem item;

    public RapierItemImpl(WeaponItem item, ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Properties settings) {
        super(buildProps(item, toolMaterial, attackDamage, attackSpeed, settings));
        this.item = item;
    }

    @SuppressWarnings("unchecked")
    private static <T> Properties buildProps(WeaponItem item, ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Properties settings) {
        settings.sword(toolMaterial, attackDamage, attackSpeed);
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
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof Player player) {
            EventActionHandler.handleHurtEnemy(target, player, item);
            if (item.armor_pierce > 0) {
                float armor = (float) target.getAttributeValue(Attributes.ARMOR);
                float pierceDamage = armor * item.armor_pierce;
                if (pierceDamage > 0) {
                    target.hurt(player.damageSources().playerAttack(player), pierceDamage);
                }
            }
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return item.information.getItemSettings().hasEnchantmentGlint.orElse(stack.isEnchanted());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        item.addLore(tooltip);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        if (!(entity instanceof Player player)) { super.onUseTick(level, entity, stack, remaining); return; }
        EventActionHandler.handleOnUseTick(player, item);
        super.onUseTick(level, entity, stack, remaining);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        EventActionHandler.handleOnUseOn(context, item);
        return super.useOn(context);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player)) return super.finishUsingItem(stack, level, entity);
        EventActionHandler.handleOnFinishUsing(player, item);
        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        if (!(entity instanceof Player player)) { super.inventoryTick(stack, level, entity, slot); return; }
        EventActionHandler.handleOnInventoryTick(player, item);
        super.inventoryTick(stack, level, entity, slot);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Player player) {
        EventActionHandler.handleOnItemCrafted(player, item);
        super.onCraftedBy(stack, player);
    }
}
