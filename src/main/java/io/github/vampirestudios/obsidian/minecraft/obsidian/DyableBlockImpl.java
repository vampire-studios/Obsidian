package io.github.vampirestudios.obsidian.minecraft.obsidian;

import io.github.vampirestudios.obsidian.api.obsidian.block.Block;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class DyableBlockImpl extends BaseEntityBlock {
	public Block block;
	private Identifier id;

	public DyableBlockImpl(Properties properties) {
		super(properties);
	}

	public DyableBlockImpl(Identifier id, Block block, Properties settings) {
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
//    public InteractionResult use(BlockState blockState_1, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult_1) {
//        BlockEntity blockEntity = Objects.requireNonNull(world).getBlockEntity(pos);
//        if (blockEntity instanceof DyableBlockEntity dyableBlockEntity) {
//            if (!world.isClientSide) {
//                if (player.getItemInHand(hand).getItem() instanceof DyeItem dyeItem) {
//                    int newColor = ColorUtil.toIntRgb(dyeItem.getDyeColor().getTextureDiffuseColors());
//                    dyableBlockEntity.setChanged();
//                    dyableBlockEntity.setColorAndSync(newColor);
//                    player.displayClientMessage(Component.literal("Dyed a block: " + newColor).withStyle(ChatFormatting.ITALIC), true);
//                    return InteractionResult.CONSUME;
//                }
//                System.out.println("Server Color: " + dyableBlockEntity.getDyeColor());
//            } else {
//                System.out.println("Client Color: " + dyableBlockEntity.getDyeColor());
//            }
//        }
//
//        if (!world.isClientSide) {
//            Item item = BuiltInRegistries.ITEM.get(block.functions.use.item);
//            if (block.functions.use.functionType.equals(Functions.Function.FunctionType.REQUIRES_SHIFTING) && player.isShiftKeyDown() && block.functions.use.predicate.matches()) {
//                Optional<CommandFunction<CommandSourceStack>> function = Objects.requireNonNull(world.getServer()).getFunctions().get(block.functions.use.function_file);
//                function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
//                return InteractionResult.SUCCESS;
//            } else if (block.functions.use.functionType.equals(Functions.Function.FunctionType.REQUIRES_ITEM) && player.getMainHandItem().getItem().equals(item) && block.functions.use.predicate.matches()) {
//                Optional<CommandFunction<CommandSourceStack>> function = Objects.requireNonNull(world.getServer()).getFunctions().get(block.functions.use.function_file);
//                function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
//                return InteractionResult.SUCCESS;
//            } else if (block.functions.use.functionType.equals(Functions.Function.FunctionType.REQUIRES_SHIFTING_AND_ITEM) && player.isShiftKeyDown() && player.getMainHandItem().getItem().equals(item) && block.functions.use.predicate.matches()) {
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
		if (!world.isClientSide() && block.functions.random_display_tick.predicate.matches()) {
			Optional<CommandFunction<CommandSourceStack>> function = Objects.requireNonNull(world.getServer()).getFunctions().get(block.functions.random_display_tick.function_file);
			function.ifPresent(commandFunction -> world.getServer().getFunctions().execute(commandFunction, world.getServer().createCommandSourceStack()));
		}
	}

	@Override
	protected ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state, boolean bl) {
		var stack = new ItemStack(this);
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof DyeableBlockEntity dyeableBlockEntity) {
			stack.set(DataComponents.DYED_COLOR, new DyedItemColor(dyeableBlockEntity.getDyeColor()));
		}
		return stack;
	}

//    public int getColor(BlockState blockState, BlockAndTintGetter blockRenderView, BlockPos blockPos, int tintIndex) {
//        if (tintIndex == 0 && blockRenderView != null) {
//            BlockEntity blockEntity = blockRenderView.getBlockEntity(blockPos);
//            if (blockEntity instanceof DyeableBlockEntity) {
//                return ((DyeableBlockEntity) blockEntity).getDyeColor();
//            }
//        }
//        return 0;
//    }

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new DyeableBlockEntity(this.id, pos, state);
	}

}