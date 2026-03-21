package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.EventActionHandler;
import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

/** Extended-reach sword — adds reach_bonus blocks to entity interaction range. */
public class LongswordItemImpl extends Item {

    public final WeaponItem item;

    public LongswordItemImpl(WeaponItem item, ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Properties settings) {
        super(buildProps(item, toolMaterial, attackDamage, attackSpeed, settings));
        this.item = item;
    }

    @SuppressWarnings("unchecked")
    private static <T> Properties buildProps(WeaponItem item, ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Properties settings) {
        settings.sword(toolMaterial, attackDamage, attackSpeed);

        if (item.reach_bonus > 0) {
            // Re-build attribute modifiers to include reach on top of the sword defaults.
            ItemAttributeModifiers extended = ItemAttributeModifiers.builder()
                    .add(Attributes.ATTACK_DAMAGE,
                            new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID,
                                    toolMaterial.attackDamageBonus() + attackDamage,
                                    AttributeModifier.Operation.ADD_VALUE),
                            EquipmentSlotGroup.MAINHAND)
                    .add(Attributes.ATTACK_SPEED,
                            new AttributeModifier(Item.BASE_ATTACK_SPEED_ID,
                                    attackSpeed,
                                    AttributeModifier.Operation.ADD_VALUE),
                            EquipmentSlotGroup.MAINHAND)
                    .add(Attributes.ENTITY_INTERACTION_RANGE,
                            new AttributeModifier(
                                    Identifier.fromNamespaceAndPath("obsidian", "longsword_reach"),
                                    item.reach_bonus,
                                    AttributeModifier.Operation.ADD_VALUE),
                            EquipmentSlotGroup.MAINHAND)
                    .build();
            settings.attributes(extended);
        }

        if (item.components != null) {
            for (var e : item.components.entrySet()) {
                var type = (DataComponentType<T>) e.getKey();
                var opt  = (Optional<T>) e.getValue();
                opt.ifPresent(v -> settings.component(type, v));
            }
        }
        return settings;
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
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!(attacker instanceof Player player)) return;
        EventActionHandler.handleHurtEnemy(target, player, item);
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
