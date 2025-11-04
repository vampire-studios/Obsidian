package io.github.vampirestudios.obsidian.minecraft.obsidian;

import com.google.common.collect.Maps;
import io.github.vampirestudios.obsidian.api.events.FlexEventHandler;
import io.github.vampirestudios.obsidian.registry.properties.ListProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BlockImpl extends Block {

    public final io.github.vampirestudios.obsidian.api.obsidian.block.Block block;
    private final Map<String, FlexEventHandler> eventHandlers = Maps.newHashMap();
    private static final Map<String, ListProperty> CUSTOM_PROPERTIES = Maps.newHashMap();

    public BlockImpl(io.github.vampirestudios.obsidian.api.obsidian.block.Block block, Properties settings) {
        super(settings);
        this.block = block;

        registerProperties();
        registerDefaultState();
    }

    private void registerProperties() {
        if (block.information.properties != null) {
            block.information.properties.forEach((key, value) -> {
                List<String> values = Arrays.asList(value); // Ensure it's treated as a list
                ListProperty property = ListProperty.create(key, values);
                CUSTOM_PROPERTIES.put(key, property);
            });
        }
    }

    private void registerDefaultState() {
        BlockState state = this.stateDefinition.any();
        for (Map.Entry<String, ListProperty> entry : CUSTOM_PROPERTIES.entrySet()) {
            ListProperty property = entry.getValue();
            String defaultValue = block.information.defaultValue.get(entry.getKey());
            if (defaultValue != null && property.getPossibleValues().contains(defaultValue)) {
                state = state.setValue(property, defaultValue);
            }
        }
        this.registerDefaultState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        if (!CUSTOM_PROPERTIES.isEmpty()) CUSTOM_PROPERTIES.forEach((_, property) -> builder.add(property));
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = super.getStateForPlacement(ctx);
        if (!CUSTOM_PROPERTIES.isEmpty()) {
            for (Map.Entry<String, ListProperty> entry : CUSTOM_PROPERTIES.entrySet()) {
                String propertyName = entry.getKey();
                ListProperty property = entry.getValue();

                String value = resolvePlacementProperty(propertyName, ctx, property);
                if (value != null && property.getPossibleValues().contains(value)) {
                    state = state.setValue(property, value);
                }
            }
        }
        return state;
    }

    // Dynamically resolve property values
    private String resolvePlacementProperty(String propertyName, BlockPlaceContext ctx, ListProperty property) {
        if (propertyName.equals("placement")) {
            Direction direction = ctx.getClickedFace();
            Direction.Axis axis = direction.getAxis();
            return axis == Direction.Axis.Y ? "floor" : "wall";
        }
        // Add more dynamic resolution logic for other properties here if needed
        return property.getPossibleValues().getFirst(); // Default to the first value
    }

    @Override
    protected void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack, boolean dropExperience) {
        super.spawnAfterBreak(state, level, pos, stack, dropExperience);
        if (dropExperience) {
            if (block.dropInformation != null) this.popExperience(level, pos, block.dropInformation.xpDropAmount);
        }
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        float defaultValue = super.getShadeBrightness(state, world, pos);
        if (block.information.getBlockSettings() != null) {
            if (block.information.getBlockSettings().getParentSettings() != null) {
                return !block.information.getBlockSettings().getParentSettings().translucent ? 0.2F : 1.0F;
            } else {
                return !block.information.getBlockSettings().translucent ? 0.2F : 1.0F;
            }
        } else {
            return defaultValue;
        }
    }

    @Override
    public boolean isCollisionShapeFullBlock(BlockState state, BlockGetter world, BlockPos pos) {
        boolean defaultValue = super.isCollisionShapeFullBlock(state, world, pos);
        if (block.information.getBlockSettings() != null) {
            if (block.information.getBlockSettings().getParentSettings() != null) {
                return !block.information.getBlockSettings().getParentSettings().translucent;
            } else {
                return !block.information.getBlockSettings().translucent;
            }
        } else {
            return defaultValue;
        }
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state) {
        boolean defaultValue = super.propagatesSkylightDown(state);
        if (block.information.getBlockSettings() != null) {
            if (block.information.getBlockSettings().getParentSettings() != null) {
                return block.information.getBlockSettings().getParentSettings().translucent;
            } else {
                return block.information.getBlockSettings().translucent;
            }
        } else {
            return defaultValue;
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
}