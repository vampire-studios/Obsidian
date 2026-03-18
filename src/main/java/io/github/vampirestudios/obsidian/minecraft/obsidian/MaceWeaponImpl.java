package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.EventActionHandler;
import io.github.vampirestudios.obsidian.api.obsidian.item.WeaponItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
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
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class MaceWeaponImpl extends MaceItem {

    public WeaponItem item;

    public MaceWeaponImpl(WeaponItem item, ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Properties settings) {
        super(buildProperties(item, toolMaterial, attackDamage, attackSpeed, settings));
        this.item = item;
    }

    private static Properties buildProperties(WeaponItem item, ToolMaterial toolMaterial, float attackDamage, float attackSpeed, Properties settings) {
        WeaponItem.MaceProperties mace = item.mace;
        Properties props = settings
                .durability(mace.durability != null ? mace.durability : toolMaterial.durability())
                .enchantable(mace.enchantability != null ? mace.enchantability : toolMaterial.enchantmentValue())
                .component(DataComponents.TOOL, MaceItem.createToolProperties())
                .attributes(buildAttributes(attackDamage, attackSpeed))
                .component(DataComponents.WEAPON, new Weapon(1));
        if (mace.repairable != null) {
            Identifier repairId = Identifier.tryParse(mace.repairable);
            if (repairId == null) throw new IllegalArgumentException("Invalid mace repair tag identifier: " + mace.repairable);
            props.repairable(TagKey.create(Registries.ITEM, repairId));
        } else {
            props.repairable(toolMaterial.repairItems());
        }
        return props;
    }

    private static ItemAttributeModifiers buildAttributes(float attackDamage, float attackSpeed) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
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
        super.hurtEnemy(stack, target, attacker); // applies smash-attack knockback & fall-damage cancellation
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
