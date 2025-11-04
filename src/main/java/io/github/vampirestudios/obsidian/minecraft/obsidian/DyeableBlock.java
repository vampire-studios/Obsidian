package io.github.vampirestudios.obsidian.minecraft.obsidian;

import com.mojang.serialization.MapCodec;
import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class DyeableBlock extends BaseEntityBlock {
    public final Block block;
    private ResourceLocation id;
    private static final MapCodec<BaseEntityBlock> CODEC = simpleCodec(DyeableBlock::new);

    public DyeableBlock(BlockBehaviour.Properties settings) {
        super(settings);
        this.block = null;
    }

    public DyeableBlock(ResourceLocation id, Block block, BlockBehaviour.Properties settings) {
        super(settings);
        this.id = id;
        this.block = block;
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
    public boolean propagatesSkylightDown(BlockState state) {
        return block.information.getBlockSettings() != null ? block.information.getBlockSettings().translucent : super.propagatesSkylightDown(state);
    }

//    @Override
//    public InteractionResult use(BlockState blockState_1, Level world, BlockPos blockPos_1, Player playerEntity_1, InteractionHand hand_1, BlockHitResult blockHitResult_1) {
//        if (!world.isClientSide && block.functions != null) {
//            if (block.functions.use == null) return InteractionResult.FAIL;
//
//            Item item = BuiltInRegistries.ITEM.get(block.functions.use.item);
//            if (block.functions.use.functionType.equals(Functions.Function.FunctionType.REQUIRES_SHIFTING) && playerEntity_1.isShiftKeyDown() && block.functions.use.predicate.matches()) {
//                Optional<CommandFunction<CommandSourceStack>> function = Objects.requireNonNull(world.getServer()).getFunctions().get(block.functions.use.function_file);
//                function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
//                return InteractionResult.SUCCESS;
//            } else if (block.functions.use.functionType.equals(Functions.Function.FunctionType.REQUIRES_ITEM) && playerEntity_1.getMainHandItem().getItem().equals(item) && block.functions.use.predicate.matches()) {
//                Optional<CommandFunction<CommandSourceStack>> function = Objects.requireNonNull(world.getServer()).getFunctions().get(block.functions.use.function_file);
//                function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
//                return InteractionResult.SUCCESS;
//            } else if (block.functions.use.functionType.equals(Functions.Function.FunctionType.REQUIRES_SHIFTING_AND_ITEM) && playerEntity_1.isShiftKeyDown() && playerEntity_1.getMainHandItem().getItem().equals(item) && block.functions.use.predicate.matches()) {
//                Optional<CommandFunction<CommandSourceStack>> function = Objects.requireNonNull(world.getServer()).getFunctions().get(block.functions.use.function_file);
//                function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
//                return InteractionResult.SUCCESS;
//            } else if (block.functions.use.functionType.equals(Functions.Function.FunctionType.NONE) &&  block.functions.use.predicate.matches()) {
//                Optional<CommandFunction<CommandSourceStack>> function = Objects.requireNonNull(world.getServer()).getFunctions().get(block.functions.use.function_file);
//                function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
//                return InteractionResult.SUCCESS;
//            }
//        }
//        return InteractionResult.FAIL;
//    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (block.functions.scheduled_tick.predicate.matches()) {
            Optional<CommandFunction<CommandSourceStack>> function = world.getServer().getFunctions().get(block.functions.scheduled_tick.function_file);
            function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (block.functions.random_tick.predicate.matches()) {
            Optional<CommandFunction<CommandSourceStack>> function = world.getServer().getFunctions().get(block.functions.random_tick.function_file);
            function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if (!world.isClientSide && block.functions.random_display_tick.predicate.matches()) {
            Optional<CommandFunction<CommandSourceStack>> function = Objects.requireNonNull(world.getServer()).getFunctions().get(block.functions.random_display_tick.function_file);
            function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
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
                return box(0, 0, 0, 16, 16, 16);
            }
        } else {
            return box(0, 0, 0, 16, 16, 16);
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
                return box(0, 0, 0, 16, 16, 16);
            }
        } else {
            return box(0, 0, 0, 16, 16, 16);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DyableBlockEntity(id, pos, state);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean bl) {
        ItemStack stack = super.getCloneItemStack(world, pos, state, bl);
        if (stack.getItem() instanceof CustomDyeableItem) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof DyableBlockEntity dyeableBlockEntity) {
                stack.set(DataComponents.DYED_COLOR, new DyedItemColor(dyeableBlockEntity.getDyeColor()));
            }
        }
        return stack;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DyableBlockEntity dyableBlockEntity) {
            int hue = itemStack.get(DataComponents.DYED_COLOR).rgb();
            if (hue != 0) {
                dyableBlockEntity.setDyeColor(hue);
            } else {
                dyableBlockEntity.setDyeColor(block.additional_information.defaultColor);
            }
        }

    }
}
