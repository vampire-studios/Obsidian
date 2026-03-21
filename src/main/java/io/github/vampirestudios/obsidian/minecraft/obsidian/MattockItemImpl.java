package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.EventActionHandler;
import io.github.vampirestudios.obsidian.api.obsidian.item.ToolItem;
import io.github.vampirestudios.obsidian.addon_modules.ItemModuleHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/** Axe + Hoe combo — chops axe blocks and tills dirt. */
public class MattockItemImpl extends Item {

    public final ToolItem item;

    public MattockItemImpl(ToolItem item, ToolMaterial material, Properties settings) {
        super(buildProps(item, material, settings));
        this.item = item;
    }

    private static Properties buildProps(ToolItem item, ToolMaterial material, Properties settings) {
        settings.axe(material, 2f, -2.0f);
        if (item.components != null) ItemModuleHelper.applyAllComponents(settings, item.components);
        return settings;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(BlockTags.MINEABLE_WITH_AXE) || state.is(BlockTags.MINEABLE_WITH_HOE)) {
            ToolMaterial mat = item.getToolMaterial();
            if (mat != null) return mat.speed();
        }
        return super.getDestroySpeed(stack, state);
    }

    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return super.isCorrectToolForDrops(stack, state) || state.is(BlockTags.MINEABLE_WITH_HOE);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);

        if (context.getClickedFace() != Direction.DOWN) {
            BlockState tilled = getTilledStateOf(state);
            if (tilled != null) {
                Player player = context.getPlayer();
                world.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0f, 1.0f);
                if (!world.isClientSide()) {
                    world.setBlock(pos, tilled, 11);
                    if (player != null) context.getItemInHand().hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                }
                return InteractionResult.SUCCESS;
            }
        }

        EventActionHandler.handleOnUseOn(context, item);
        return super.useOn(context);
    }

    static @Nullable BlockState getTilledStateOf(BlockState state) {
        if (state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT_PATH))
            return Blocks.FARMLAND.defaultBlockState();
        if (state.is(Blocks.COARSE_DIRT))
            return Blocks.DIRT.defaultBlockState();
        return null;
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
    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
        if (!(miningEntity instanceof Player player)) return super.mineBlock(stack, level, state, pos, miningEntity);
        EventActionHandler.handleOnMiningBlock(player, state, pos, item, this);
        return super.mineBlock(stack, level, state, pos, miningEntity);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        if (!(entity instanceof Player player)) { super.onUseTick(level, entity, stack, remaining); return; }
        EventActionHandler.handleOnUseTick(player, item);
        super.onUseTick(level, entity, stack, remaining);
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
