package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.EventActionHandler;
import io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem;
import io.github.vampirestudios.obsidian.addon_modules.ItemModuleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** 3×3 pickaxe — heavy damage, slow swing. */
public class HammerItemImpl extends Item {

    public final ToolItem item;

    public HammerItemImpl(ToolItem item, ToolMaterial material, Properties settings) {
        super(buildProps(item, material, settings));
        this.item = item;
    }

    private static Properties buildProps(ToolItem item, ToolMaterial material, Properties settings) {
        settings.pickaxe(material, 5f, -3.6f);
        if (item.components != null) ItemModuleHelper.applyAllComponents(settings, item.components);
        return settings;
    }

    @Override
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (!(miningEntity instanceof Player player)) return super.mineBlock(stack, level, state, pos, miningEntity);
        if (!level.isClientSide()) {
            for (BlockPos areaPos : getAreaPositions(pos, player, item.mining_radius)) {
                BlockState areaState = level.getBlockState(areaPos);
                if (!areaState.isAir() && areaState.is(BlockTags.MINEABLE_WITH_PICKAXE)
                        && stack.isCorrectToolForDrops(areaState)) {
                    level.destroyBlock(areaPos, !player.isCreative(), player);
                    stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                }
            }
        }
        EventActionHandler.handleOnMiningBlock(player, state, pos, item, this);
        return super.mineBlock(stack, level, state, pos, miningEntity);
    }

    static List<BlockPos> getAreaPositions(BlockPos center, LivingEntity miner, int radius) {
        Vec3 look = miner.getLookAngle();
        Direction facing = Direction.getApproximateNearest(look.x, look.y, look.z);
        Direction.Axis axis = facing.getAxis();
        List<BlockPos> positions = new ArrayList<>();
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                if (i == 0 && j == 0) continue;
                positions.add(switch (axis) {
                    case Y -> center.offset(i, 0, j);
                    case X -> center.offset(0, i, j);
                    case Z -> center.offset(i, j, 0);
                });
            }
        }
        return positions;
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
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        EventActionHandler.handleOnUse(user, item);
        return InteractionResult.PASS;
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
