/*
package io.github.vampirestudios.obsidian;

import blue.endless.jankson.*;
import io.github.cottonmc.jankson.BlockAndItemSerializers;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.util.valueproviders.FloatProviderType;
import net.minecraft.util.valueproviders.IntProviderType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.score.LootScoreProviderType;

public class JanksonFactory {
	public static Jankson.Builder builder() {
		Jankson.Builder builder = Jankson.builder();

		builder
				.registerDeserializer(String.class, ItemStack.class, BlockAndItemSerializers::getBlockStatePrimitive)
				.registerDeserializer(JsonObject.class, ItemStack.class, BlockAndItemSerializers::getItemStack)
				.registerSerializer(ItemStack.class, BlockAndItemSerializers::saveItemStack);

		builder
				.registerDeserializer(String.class, BlockState.class, BlockAndItemSerializers::getBlockStatePrimitive)
				.registerDeserializer(JsonObject.class, BlockState.class, BlockAndItemSerializers::getBlockState)
				.registerSerializer(BlockState.class, BlockAndItemSerializers::saveBlockState);

		builder
				.registerDeserializer(String.class, Identifier.class, (s, m) -> Identifier.parse(s))
				.registerSerializer(Identifier.class, (i,m)->new JsonPrimitive(i.toString()))
		;

		//All the things you could potentially specify with just a registry ID
		//Note: specifically excludes dynamic registries since we can't have static access to them.
		register(builder, Activity.class, BuiltInRegistries.ACTIVITY);
		register(builder, ArgumentTypeInfo.class, BuiltInRegistries.COMMAND_ARGUMENT_TYPE);
		register(builder, Block.class, BuiltInRegistries.BLOCK);
		register(builder, BlockEntityType.class, BuiltInRegistries.BLOCK_ENTITY_TYPE);
		register(builder, BlockPredicateType.class, BuiltInRegistries.BLOCK_PREDICATE_TYPE);
		register(builder, BlockStateProviderType.class, BuiltInRegistries.BLOCKSTATE_PROVIDER_TYPE);
		register(builder, WorldCarver.class, BuiltInRegistries.CARVER);
		register(builder, CatVariant.class, BuiltInRegistries.CAT_VARIANT);
		register(builder, ChunkStatus.class, BuiltInRegistries.CHUNK_STATUS);
		register(builder, Attribute.class, BuiltInRegistries.ATTRIBUTE);
		register(builder, EntityType.class, BuiltInRegistries.ENTITY_TYPE);
		register(builder, Feature.class, BuiltInRegistries.FEATURE);
		register(builder, FeatureSizeType.class, BuiltInRegistries.FEATURE_SIZE_TYPE);
		register(builder, FloatProviderType.class, BuiltInRegistries.FLOAT_PROVIDER_TYPE);
		register(builder, Fluid.class, BuiltInRegistries.FLUID);
		register(builder, FoliagePlacerType.class, BuiltInRegistries.FOLIAGE_PLACER_TYPE);
		register(builder, FrogVariant.class, BuiltInRegistries.FROG_VARIANT);
		register(builder, GameEvent.class, BuiltInRegistries.GAME_EVENT);
		register(builder, HeightProviderType.class, BuiltInRegistries.HEIGHT_PROVIDER_TYPE);
		register(builder, IntProviderType.class, BuiltInRegistries.INT_PROVIDER_TYPE);
		register(builder, Item.class, BuiltInRegistries.ITEM);
		register(builder, DataComponentType.class, BuiltInRegistries.DATA_COMPONENT_TYPE);
		register(builder, LootItemConditionType.class, BuiltInRegistries.LOOT_CONDITION_TYPE);
		register(builder, LootItemFunctionType.class, BuiltInRegistries.LOOT_FUNCTION_TYPE);
		register(builder, LootNbtProviderType.class, BuiltInRegistries.LOOT_NBT_PROVIDER_TYPE);
		register(builder, LootNumberProviderType.class, BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE);
		register(builder, LootPoolEntryType.class, BuiltInRegistries.LOOT_POOL_ENTRY_TYPE);
		register(builder, LootScoreProviderType.class, BuiltInRegistries.LOOT_SCORE_PROVIDER_TYPE);
		register(builder, MemoryModuleType.class, BuiltInRegistries.MEMORY_MODULE_TYPE);
		register(builder, ParticleType.class, BuiltInRegistries.PARTICLE_TYPE);
		register(builder, PlacementModifierType.class, BuiltInRegistries.PLACEMENT_MODIFIER_TYPE);
		register(builder, PoiType.class, BuiltInRegistries.POINT_OF_INTEREST_TYPE);
		register(builder, PositionSourceType.class, BuiltInRegistries.POSITION_SOURCE_TYPE);
		register(builder, PosRuleTestType.class, BuiltInRegistries.POS_RULE_TEST);
		register(builder, Potion.class, BuiltInRegistries.POTION);
		register(builder, RecipeSerializer.class, BuiltInRegistries.RECIPE_SERIALIZER);
		register(builder, RecipeType.class, BuiltInRegistries.RECIPE_TYPE);
		register(builder, RootPlacerType.class, BuiltInRegistries.ROOT_PLACER_TYPE);
		register(builder, RuleTestType.class, BuiltInRegistries.RULE_TEST);
		register(builder, Schedule.class, BuiltInRegistries.SCHEDULE);
		register(builder, MenuType.class, BuiltInRegistries.MENU);
		register(builder, SensorType.class, BuiltInRegistries.SENSOR_TYPE);
		register(builder, SoundEvent.class, BuiltInRegistries.SOUND_EVENT);
		register(builder, StatType.class, BuiltInRegistries.STAT_TYPE);
		register(builder, MobEffect.class, BuiltInRegistries.MOB_EFFECT);
		register(builder, StructurePlacementType.class, BuiltInRegistries.STRUCTURE_PLACEMENT);
		register(builder, StructurePieceType.class, BuiltInRegistries.STRUCTURE_PIECE);
		register(builder, StructurePoolElementType.class, BuiltInRegistries.STRUCTURE_POOL_ELEMENT);
		register(builder, StructureProcessorType.class, BuiltInRegistries.STRUCTURE_PROCESSOR);
		register(builder, StructureType.class, BuiltInRegistries.STRUCTURE_TYPE);
		register(builder, TreeDecoratorType.class, BuiltInRegistries.TREE_DECORATOR_TYPE);
		register(builder, TrunkPlacerType.class, BuiltInRegistries.TRUNK_PLACER_TYPE);
		register(builder, VillagerProfession.class, BuiltInRegistries.VILLAGER_PROFESSION);
		register(builder, VillagerType.class, BuiltInRegistries.VILLAGER_TYPE);
		register(builder, Registry.class, BuiltInRegistries.REGISTRY);

		return builder;
	}

	private static <T> void register(Jankson.Builder builder, Class<T> clazz, Registry<? extends T> registry) {
		builder.registerDeserializer(String.class, clazz, (s, m) -> lookupDeserialize(s, registry));
		builder.registerSerializer(clazz, (o, m) -> lookupSerialize(o, registry));
	}

	private static <T> T lookupDeserialize(String s, Registry<T> registry) {
		return registry.getValue(Identifier.tryParse(s));
	}

	private static <T, U extends T> JsonElement lookupSerialize(T t, Registry<U> registry) {
		@SuppressWarnings("unchecked") //Widening cast happening because of generic type parameters in the registry class
		Identifier id = registry.getKey((U) t);
		if (id == null) return JsonNull.INSTANCE;
		return new JsonPrimitive(id.toString());
	}


	public static Jankson createJankson() {
		return builder().build();
	}

}
*/
