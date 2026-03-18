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
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

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
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape resolved = resolveShape(block.information.collisionShape, block.information.shape, block.information.shapes);
        return resolved != null ? resolved : Shapes.block();
    }

    @Override
    @NullMarked
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        VoxelShape resolved = resolveShape(block.information.outlineShape, block.information.shape, block.information.shapes);
        return resolved != null ? resolved : Shapes.block();
    }

    @Override
    @NullMarked
    protected VoxelShape getOcclusionShape(BlockState state) {
        // Use the actual block shape for AO/occlusion so non-full blocks
        // don't darken the faces of blocks around them.
        VoxelShape resolved = resolveShape(block.information.outlineShape, block.information.shape, block.information.shapes);
        return resolved != null ? resolved : Shapes.block();
    }

    /**
     * Resolves a VoxelShape from a BoundingBox, falling back to the multi-box shorthand,
     * then the single-box shorthand.
     */
    private VoxelShape resolveShape(io.github.vampirestudios.obsidian.api.obsidian.block.BlockInformation.BoundingBox specific, float[] shorthand, float[][] shorthands) {
        if (specific != null && specific.collisionType != null) {
            return switch (specific.collisionType) {
                case FULL_BLOCK -> Shapes.block();
                case BOTTOM_SLAB -> box(0, 0, 0, 16, 8, 16);
                case TOP_SLAB -> box(0, 8, 0, 16, 16, 16);
                case CUSTOM -> {
                    // Multi-box takes priority over single-box
                    if (specific.full_shapes != null) yield createCompositeShape(specific.full_shapes);
                    float[] s = specific.full_shape;
                    yield box(s[0], s[1], s[2], s[3], s[4], s[5]);
                }
                case NONE -> Shapes.empty();
            };
        }
        // Multi-box shorthand takes priority over single-box shorthand
        if (shorthands != null) return createCompositeShape(shorthands);
        if (shorthand != null) return box(shorthand[0], shorthand[1], shorthand[2], shorthand[3], shorthand[4], shorthand[5]);
        return null;
    }

    /** Combines multiple boxes into a single composite VoxelShape using Shapes.or(). */
    private VoxelShape createCompositeShape(float[][] boxes) {
        VoxelShape result = Shapes.empty();
        for (float[] b : boxes) {
            result = Shapes.or(result, box(b[0], b[1], b[2], b[3], b[4], b[5]));
        }
        return result;
    }
}