package io.github.vampirestudios.obsidian.minecraft.obsidian;

import com.google.common.collect.Maps;
import io.github.vampirestudios.obsidian.api.events.FlexEventContext;
import io.github.vampirestudios.obsidian.api.events.FlexEventHandler;
import io.github.vampirestudios.obsidian.api.events.FlexEventResult;
import io.github.vampirestudios.obsidian.api.events.IEventRunner;
import io.github.vampirestudios.obsidian.api.obsidian.TooltipInformation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class BlockImpl extends Block implements IEventRunner {

    public final io.github.vampirestudios.obsidian.api.obsidian.block.Block block;
    private final Map<String, FlexEventHandler> eventHandlers = Maps.newHashMap();

    public BlockImpl(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Properties settings) {
        super(settings);
        this.block = block;
//        initializeFlex(propertyDefaultValues);
    }


    @SuppressWarnings({"rawtypes", "unchecked"})
    private void initializeFlex(Map<Property<?>, Comparable<?>> propertyDefaultValues) {
        if (propertyDefaultValues.size() > 0) {
            BlockState def = getStateDefinition().any();
            for (Map.Entry<Property<?>, Comparable<?>> entry : propertyDefaultValues.entrySet()) {
                Property prop = entry.getKey();
                Comparable value = entry.getValue();
                def = def.setValue(prop, value);
            }

            registerDefaultState(def);
        }
    }

    @Override
    public void spawnAfterBreak(BlockState state, ServerLevel world, BlockPos pos, ItemStack stack, boolean dropExperience) {
        super.spawnAfterBreak(state, world, pos, stack, dropExperience);
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0) {
            if (block.dropInformation != null) this.popExperience(world, pos, block.dropInformation.xpDropAmount);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return runEvent("use", FlexEventContext.of(level, pos, state)
                .withHand(player, hand)
                .withRayTrace(hit), () -> FlexEventResult.of(super.use(state, level, pos, player, hand, hit))).result();
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return block.information.getBlockSettings() != null ? !block.information.getBlockSettings().translucent ? 0.2F : 1.0F : super.getShadeBrightness(state, world, pos);
    }

    @Override
    public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return block.information.getBlockSettings() != null ? !block.information.getBlockSettings().translucent : super.isCollisionShapeFullBlock(state, world, pos);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return block.information.getBlockSettings() != null ? block.information.getBlockSettings().translucent : super.propagatesSkylightDown(state, world, pos);
    }

    @Override
    public void appendHoverText(ItemStack stack, BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        if (block.lore != null && block.lore.length != 0) {
            for (TooltipInformation tooltipInformation : block.lore) {
                tooltip.add(tooltipInformation.getTextType("tooltip"));
            }
        }
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (block.information.collisionShape != null) {
            if(block.information.collisionShape.collisionType != null) {
                return switch(block.information.collisionShape.collisionType) {
                    case FULL_BLOCK -> Shapes.block();
                    case BOTTOM_SLAB -> box(0, 0, 0, 16, 8.0, 16);
                    case TOP_SLAB -> box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
                    case CUSTOM -> {
                        float[] boundingBox = block.information.collisionShape.full_shape;
                        yield box(boundingBox[0], boundingBox[1], boundingBox[2], boundingBox[3], boundingBox[4], boundingBox[5]);
                    }
                    case NONE -> Shapes.empty();
                };
            } else {
                return Shapes.block();
            }
        } else {
            return Shapes.block();
        }
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (block.information.outlineShape != null) {
            if(block.information.outlineShape.collisionType != null) {
                return switch(block.information.outlineShape.collisionType) {
                    case FULL_BLOCK -> Shapes.block();
                    case BOTTOM_SLAB -> box(0, 0, 0, 16, 8.0, 16);
                    case TOP_SLAB -> box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
                    case CUSTOM -> {
                        float[] boundingBox = block.information.outlineShape.full_shape;
                        yield box(boundingBox[0], boundingBox[1], boundingBox[2], boundingBox[3], boundingBox[4], boundingBox[5]);
                    }
                    case NONE -> Shapes.empty();
                };
            } else {
                return Shapes.block();
            }
        } else {
            return Shapes.block();
        }
    }

    @Override
    public void addEventHandler(String eventName, FlexEventHandler eventHandler) {
        eventHandlers.put(eventName, eventHandler);
    }

    @Nullable
    @Override
    public FlexEventHandler getEventHandler(String eventName) {
        return eventHandlers.get(eventName);
    }
}