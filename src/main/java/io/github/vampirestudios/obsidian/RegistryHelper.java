//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.github.vampirestudios.obsidian;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

public record RegistryHelper(String modId) {
	public static RegistryHelper createRegistryHelper(String modId) {
		return new RegistryHelper(modId);
	}

	public static Block[] collectBlocks(Class<?> blockClass) {
		Stream<Block> var10000 = BuiltInRegistries.BLOCK.stream();
		Objects.requireNonNull(blockClass);
		return var10000.filter(blockClass::isInstance).toArray(Block[]::new);
	}

	public Blocks blocks() {
		return new Blocks(this.modId());
	}

	public Items items() {
		return new Items(this.modId());
	}

	public <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(FabricBlockEntityTypeBuilder.Factory<T> blockEntityType, Class<? extends Block> block, String name) {
		FabricBlockEntityTypeBuilder<T> builder = FabricBlockEntityTypeBuilder.create(blockEntityType, collectBlocks(block));
		return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(this.modId, name), builder.build());
	}

	public <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(FabricBlockEntityTypeBuilder<T> builder, String name) {
		return (BlockEntityType) this.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, name, builder.build());
	}

	public <T extends Entity> EntityType<T> registerEntity(FabricEntityTypeBuilder<T> builder, String name) {
		return (EntityType) this.register(BuiltInRegistries.ENTITY_TYPE, name, builder.build(ResourceKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(this.modId(), name))));
	}

	public SoundEvent createSoundEvent(String name) {
		return this.register(BuiltInRegistries.SOUND_EVENT, name, SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(this.modId, name)));
	}

	public SoundEvent registerSoundEvent(SoundEvent soundEvent, String name) {
		return this.register(BuiltInRegistries.SOUND_EVENT, name, soundEvent);
	}

	private <T> T register(Registry<T> registry, String name, T object) {
		return Registry.register(registry, ResourceLocation.fromNamespaceAndPath(this.modId(), name), object);
	}

	public static class Blocks {
		protected final String modId;

		public Blocks(String modId) {
			this.modId = modId;
		}

		public Block registerBlock(Block block, String name) {
			return this.registerBlock(block, name, CreativeModeTabs.BUILDING_BLOCKS);
		}

		public Block registerBlock(Block block, String name, ResourceKey<CreativeModeTab> itemGroup) {
			block = registerBlockWithoutItem(name, block);
			Item item = this.register(BuiltInRegistries.ITEM, name, new BlockItem(block, new Item.Properties()));
			ItemGroupEvents.modifyEntriesEvent(itemGroup).register((entries) -> entries.accept(item));
			return block;
		}

		@SafeVarargs
		public final Block registerBlock(Block block, String name, ResourceKey<CreativeModeTab>... itemGroups) {
			block = registerBlockWithoutItem(name, block);
			Item item = this.register(BuiltInRegistries.ITEM, name, new BlockItem(block, new Item.Properties()));

			for (ResourceKey<CreativeModeTab> itemGroup : itemGroups) {
				ItemGroupEvents.modifyEntriesEvent(itemGroup).register((entries) -> entries.accept(item));
			}

			return block;
		}

		public Block registerBlock(Block block, String name, ResourceKey<CreativeModeTab> itemGroup, Block parentBlock) {
			this.register(BuiltInRegistries.BLOCK, name, block);
			Item item = this.register(BuiltInRegistries.ITEM, name, new BlockItem(block, new Item.Properties()));
			if (parentBlock != null) {
				ItemGroupEvents.modifyEntriesEvent(itemGroup).register((entries) -> entries.addAfter(parentBlock, item));
			}

			return block;
		}

		public Block registerBlockWood(Block block, String name, ResourceKey<CreativeModeTab> itemGroup, Block parentBlock) {
			this.register(BuiltInRegistries.BLOCK, name, block);
			Item item = this.register(BuiltInRegistries.ITEM, name, new BlockItem(block, new Item.Properties()));
			if (parentBlock != null) {
				ItemGroupEvents.modifyEntriesEvent(itemGroup).register((entries) -> entries.addBefore(parentBlock, item));
			}

			return block;
		}

		public Block registerDoubleBlock(Block block, String name, ResourceKey<CreativeModeTab> itemGroup) {
			this.register(BuiltInRegistries.BLOCK, name, block);
			Item item = this.register(BuiltInRegistries.ITEM, name, new DoubleHighBlockItem(block, new Item.Properties()));
			ItemGroupEvents.modifyEntriesEvent(itemGroup).register((entries) -> entries.accept(item));
			return block;
		}

		public Block registerDoubleBlock(Block block, String name, ResourceKey<CreativeModeTab> itemGroup, Block parentBlock) {
			this.register(BuiltInRegistries.BLOCK, name, block);
			Item item = this.register(BuiltInRegistries.ITEM, name, new DoubleHighBlockItem(block, new Item.Properties()));
			ItemGroupEvents.modifyEntriesEvent(itemGroup).register((entries) -> entries.addAfter(parentBlock, item));
			return block;
		}

		public Block registerBlockWithoutCreativeTab(Block block, String name) {
			this.register(BuiltInRegistries.BLOCK, name, block);
			this.register(BuiltInRegistries.ITEM, name, new BlockItem(block, new Item.Properties()));
			return block;
		}

		@SafeVarargs
		public final Block registerBlock(Block block, String name, Block parentBlock, ResourceKey<CreativeModeTab>... itemGroups) {
			Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(this.modId, name), block);
			Item item = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(this.modId, name), new BlockItem(block, new Item.Properties()));

			for (ResourceKey<CreativeModeTab> itemGroup : itemGroups) {
				ItemGroupEvents.modifyEntriesEvent(itemGroup).register((entries) -> entries.addAfter(parentBlock, item));
			}

			return block;
		}

		@SafeVarargs
		public final Block registerBlockWood(Block block, String name, Block parentBlock, ResourceKey<CreativeModeTab>... itemGroups) {
			Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(this.modId, name), block);
			Item item = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(this.modId, name), new BlockItem(block, new Item.Properties()));

			for (ResourceKey<CreativeModeTab> itemGroup : itemGroups) {
				ItemGroupEvents.modifyEntriesEvent(itemGroup).register((entries) -> entries.addBefore(parentBlock, item));
			}

			return block;
		}

		public Block registerBlock(Block block, String name, Map<ItemLike, ResourceKey<CreativeModeTab>> itemGroups) {
			Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(this.modId, name), block);
			Item item = Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(this.modId, name), new BlockItem(block, new Item.Properties()));
			itemGroups.forEach((block1, creativeModeTab) -> ItemGroupEvents.modifyEntriesEvent(creativeModeTab).register((entries) -> entries.addAfter(block1, item)));
			return block;
		}

		public Block registerBlockWithWallBlock(Block block, Block wallBlock, String name) {
			this.register(BuiltInRegistries.BLOCK, name, block);
			Item item = new StandingAndWallBlockItem(block, wallBlock, Direction.DOWN, new Item.Properties());
			this.register(BuiltInRegistries.ITEM, name, item);
			ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register((entries) -> entries.accept(item));
			return block;
		}

		public Block registerBlockWithoutItem(String name, Block block) {
			this.register(BuiltInRegistries.BLOCK, name, block);
			return block;
		}

		protected <T> T register(Registry<T> registry, String name, T object) {
			return Registry.register(registry, ResourceLocation.fromNamespaceAndPath(this.modId, name), object);
		}

		protected <T> T register(Registry<T> registry, ResourceKey<T> name, T object) {
			return Registry.register(registry, name, object);
		}
	}

	public static class Items {
		private final String modId;

		public Items(String modId) {
			this.modId = modId;
		}

		public Item registerItem(String name, Item item) {
			return this.register(BuiltInRegistries.ITEM, name, item);
		}

		public Item registerItem(String name, Function<Item.Properties, Item> function, Item.Properties properties, ResourceKey<CreativeModeTab> creativeModeTab) {
			Item item = function.apply(properties.setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(this.modId, name))));
			if (item instanceof BlockItem blockItem) {
				blockItem.registerBlocks(Item.BY_BLOCK, item);
			}
			return registerItem(name, item, creativeModeTab);
		}

		public Item registerItem(String name, Function<Item.Properties, Item> function, Item.Properties properties, ResourceKey<CreativeModeTab> creativeModeTab, Item vanillaItem) {
			Item item = function.apply(properties.setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(this.modId, name))));
			if (item instanceof BlockItem blockItem) {
				blockItem.registerBlocks(Item.BY_BLOCK, item);
			}
			return registerItem(name, item, creativeModeTab, vanillaItem);
		}

		public Item registerItem(String name, Item.Properties properties, ResourceKey<CreativeModeTab> creativeModeTab) {
			return registerItem(name, Item::new, properties, creativeModeTab);
		}

		public Item registerItem(String name, Item.Properties properties, ResourceKey<CreativeModeTab> creativeModeTab, Item vanillaItem) {
			return registerItem(name, Item::new, properties, creativeModeTab, vanillaItem);
		}

		public Item registerItem(String name, Function<Item.Properties, Item> function, ResourceKey<CreativeModeTab> creativeModeTab) {
			return registerItem(name, function, new Item.Properties(), creativeModeTab);
		}

		public Item registerItem(String name, Function<Item.Properties, Item> function, ResourceKey<CreativeModeTab> creativeModeTab, Item vanillaItem) {
			return registerItem(name, function, new Item.Properties(), creativeModeTab, vanillaItem);
		}

		public Item registerItem(String name, ResourceKey<CreativeModeTab> creativeModeTab) {
			return registerItem(name, new Item.Properties(), creativeModeTab);
		}

		public Item registerItem(String name, ResourceKey<CreativeModeTab> creativeModeTab, Item vanillaItem) {
			return registerItem(name, new Item.Properties(), creativeModeTab, vanillaItem);
		}

		public Item registerItem(String name, Item item, ResourceKey<CreativeModeTab> creativeModeTab, Item vanillaItem) {
			Item registeredItem = this.register(BuiltInRegistries.ITEM, name, item);
			ItemGroupEvents.modifyEntriesEvent(creativeModeTab).register((entries) -> entries.addAfter(vanillaItem, registeredItem));
			return registeredItem;
		}

		public Item registerItem(String name, Item item, ResourceKey<CreativeModeTab> creativeModeTab) {
			Item registeredItem = this.register(BuiltInRegistries.ITEM, name, item);
			ItemGroupEvents.modifyEntriesEvent(creativeModeTab).register((entries) -> entries.accept(registeredItem));
			return registeredItem;
		}

		private <T> T register(Registry<T> registry, String name, T object) {
			return Registry.register(registry, ResourceLocation.fromNamespaceAndPath(this.modId, name), object);
		}
	}
}