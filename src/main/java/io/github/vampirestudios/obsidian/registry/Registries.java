package io.github.vampirestudios.obsidian.registry;

import com.mojang.serialization.Lifecycle;
import io.github.vampirestudios.obsidian.Const;
import io.github.vampirestudios.obsidian.api.MapColors;
import io.github.vampirestudios.obsidian.api.SubItemGroup;
import io.github.vampirestudios.obsidian.api.VanillaSoundEvents;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.*;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.annotations.BreakDoorAnnotationComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.annotations.OpenDoorAnnotationComponent;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.behaviour.*;
import io.github.vampirestudios.obsidian.api.obsidian.entity.components.movement.BasicMovementComponent;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.WoodType;

import static io.github.vampirestudios.obsidian.Obsidian.registerInRegistryVanilla;

public class Registries {
	public static final ResourceKey<Registry<Registry<?>>> THING_REGISTRIES_REGISTRY = createKey("registries");
	public static final ResourceKey<Registry<FoodProperties>> FOOD_REGISTRY = createKey("food");
	public static final ResourceKey<Registry<Property<?>>> PROPERTY_REGISTRY = createKey("property");
	//    public static final ResourceKey<Registry<TabbedGroup>> EXPANDED_ITEM_GROUPS_REGISTRY = createKey("expanded_creative_tabs");
	public static final ResourceKey<Registry<SubItemGroup>> SUB_ITEM_GROUPS_REGISTRY = createKey("sub_item_groups");
	//    public static final ResourceKey<Registry<FlexBlockType<?>>> BLOCK_TYPE_REGISTRY = createKey("block_types");
//    public static final ResourceKey<Registry<FlexItemType<?>>> ITEM_TYPE_REGISTRY = createKey("item_types");
//    public static final ResourceKey<Registry<FlexFluidType<?>>> FLUID_TYPE_REGISTRY = createKey("fluid_types");
	public static final ResourceKey<Registry<Class<? extends Component>>> ENTITY_COMPONENT_REGISTRY = createKey("entity_components");

	public static final Registry<Registry<?>> OBSIDIAN_REGISTRIES = new MappedRegistry<>(THING_REGISTRIES_REGISTRY, Lifecycle.experimental(), false);
	public static final Registry<FoodProperties> FOODS = makeRegistry(FOOD_REGISTRY);
	public static final Registry<Property<?>> PROPERTIES = makeRegistry(PROPERTY_REGISTRY);
	public static final Registry<Class<? extends Component>> ENTITY_COMPONENTS = makeRegistry(ENTITY_COMPONENT_REGISTRY);

	public static final Registry<AddonModule> ADDON_MODULE_REGISTRY;
	public static final Registry<SubItemGroup> SUB_ITEM_GROUPS;
	public static Registry<AnimationDefinition> ANIMATION_DEFINITIONS;
	public static Registry<AnimationChannel.Interpolation> ANIMATION_CHANNEL_INTERPOLATIONS;
	public static Registry<AnimationChannel.Target> ANIMATION_CHANNEL_TARGETS;
	public static Registry<BlockSetType> BLOCK_SET_TYPES;
	public static Registry<WoodType> WOOD_TYPES;

	static {
		registerFoods();
		registerEntityComponents();
		registerProperties();
		registerBedrockBlockEvent();
		MapColors.init();
		VanillaSoundEvents.init();

		ADDON_MODULE_REGISTRY = new MappedRegistry<>(ResourceKey.createRegistryKey(Const.id("addon_modules")), Lifecycle.stable(), false);
		SUB_ITEM_GROUPS = new MappedRegistry<>(SUB_ITEM_GROUPS_REGISTRY, Lifecycle.stable(), false);
//        ANIMATION_DEFINITIONS = FabricRegistryBuilder.create(AnimationDefinition.class, Const.vanillaId("animation_definitions")).buildAndRegister();
//        ANIMATION_CHANNEL_INTERPOLATIONS = FabricRegistryBuilder.create(AnimationChannel.Interpolation.class, Const.vanillaId("animation_channel_interpolations")).buildAndRegister();
//        ANIMATION_CHANNEL_TARGETS = FabricRegistryBuilder.create(AnimationChannel.Target.class, Const.vanillaId("animation_channel_targets")).buildAndRegister();
		BLOCK_SET_TYPES = FabricRegistryBuilder.create(BlockSetType.class, Const.vanillaId("block_set_types")).buildAndRegister();
		WOOD_TYPES = FabricRegistryBuilder.create(WoodType.class, Const.vanillaId("wood_types")).buildAndRegister();
	}

	private static <T> ResourceKey<Registry<T>> createKey(String name) {
		return ResourceKey.createRegistryKey(Const.id(name));
	}

	private static <T> Registry<T> makeRegistry(ResourceKey<Registry<T>> key) {
		MappedRegistry<T> registry = new MappedRegistry<>(key, Lifecycle.stable(), false);
		return Registry.register(OBSIDIAN_REGISTRIES, key.identifier().toString(), registry);
	}

	private static void registerProperties() {
		Registry.register(PROPERTIES, "attached", BlockStateProperties.ATTACHED);
		Registry.register(PROPERTIES, "bottom", BlockStateProperties.BOTTOM);
		Registry.register(PROPERTIES, "conditional", BlockStateProperties.CONDITIONAL);
		Registry.register(PROPERTIES, "disarmed", BlockStateProperties.DISARMED);
		Registry.register(PROPERTIES, "drag", BlockStateProperties.DRAG);
		Registry.register(PROPERTIES, "enabled", BlockStateProperties.ENABLED);
		Registry.register(PROPERTIES, "extended", BlockStateProperties.EXTENDED);
		Registry.register(PROPERTIES, "eye", BlockStateProperties.EYE);
		Registry.register(PROPERTIES, "falling", BlockStateProperties.FALLING);
		Registry.register(PROPERTIES, "hanging", BlockStateProperties.HANGING);
		Registry.register(PROPERTIES, "has_bottle_0", BlockStateProperties.HAS_BOTTLE_0);
		Registry.register(PROPERTIES, "has_bottle_1", BlockStateProperties.HAS_BOTTLE_1);
		Registry.register(PROPERTIES, "has_bottle_2", BlockStateProperties.HAS_BOTTLE_2);
		Registry.register(PROPERTIES, "has_record", BlockStateProperties.HAS_RECORD);
		Registry.register(PROPERTIES, "has_book", BlockStateProperties.HAS_BOOK);
		Registry.register(PROPERTIES, "inverted", BlockStateProperties.INVERTED);
		Registry.register(PROPERTIES, "in_wall", BlockStateProperties.IN_WALL);
		Registry.register(PROPERTIES, "lit", BlockStateProperties.LIT);
		Registry.register(PROPERTIES, "locked", BlockStateProperties.LOCKED);
		Registry.register(PROPERTIES, "occupied", BlockStateProperties.OCCUPIED);
		Registry.register(PROPERTIES, "open", BlockStateProperties.OPEN);
		Registry.register(PROPERTIES, "persistent", BlockStateProperties.PERSISTENT);
		Registry.register(PROPERTIES, "powered", BlockStateProperties.POWERED);
		Registry.register(PROPERTIES, "short", BlockStateProperties.SHORT);
		Registry.register(PROPERTIES, "signal_fire", BlockStateProperties.SIGNAL_FIRE);
		Registry.register(PROPERTIES, "snowy", BlockStateProperties.SNOWY);
		Registry.register(PROPERTIES, "triggered", BlockStateProperties.TRIGGERED);
		Registry.register(PROPERTIES, "unstable", BlockStateProperties.UNSTABLE);
		Registry.register(PROPERTIES, "waterlogged", BlockStateProperties.WATERLOGGED);
		Registry.register(PROPERTIES, "berries", BlockStateProperties.BERRIES);
		Registry.register(PROPERTIES, "bloom", BlockStateProperties.BLOOM);
		Registry.register(PROPERTIES, "shrieking", BlockStateProperties.SHRIEKING);
		Registry.register(PROPERTIES, "can_summon", BlockStateProperties.CAN_SUMMON);
		Registry.register(PROPERTIES, "horizontal_axis", BlockStateProperties.HORIZONTAL_AXIS);
		Registry.register(PROPERTIES, "axis", BlockStateProperties.AXIS);
		Registry.register(PROPERTIES, "up", BlockStateProperties.UP);
		Registry.register(PROPERTIES, "down", BlockStateProperties.DOWN);
		Registry.register(PROPERTIES, "north", BlockStateProperties.NORTH);
		Registry.register(PROPERTIES, "east", BlockStateProperties.EAST);
		Registry.register(PROPERTIES, "south", BlockStateProperties.SOUTH);
		Registry.register(PROPERTIES, "west", BlockStateProperties.WEST);
		Registry.register(PROPERTIES, "facing", BlockStateProperties.FACING);
		Registry.register(PROPERTIES, "facing_except_up", BlockStateProperties.FACING_HOPPER);
		Registry.register(PROPERTIES, "horizontal_facing", BlockStateProperties.HORIZONTAL_FACING);
		Registry.register(PROPERTIES, "flower_amount", BlockStateProperties.FLOWER_AMOUNT);
		Registry.register(PROPERTIES, "orientation", BlockStateProperties.ORIENTATION);
		Registry.register(PROPERTIES, "face", BlockStateProperties.ATTACH_FACE);
		Registry.register(PROPERTIES, "bell_attachment", BlockStateProperties.BELL_ATTACHMENT);
		Registry.register(PROPERTIES, "wall_height_east", BlockStateProperties.EAST_WALL);
		Registry.register(PROPERTIES, "wall_height_north", BlockStateProperties.NORTH_WALL);
		Registry.register(PROPERTIES, "wall_height_south", BlockStateProperties.SOUTH_WALL);
		Registry.register(PROPERTIES, "wall_height_west", BlockStateProperties.WEST_WALL);
		Registry.register(PROPERTIES, "redstone_east", BlockStateProperties.EAST_REDSTONE);
		Registry.register(PROPERTIES, "redstone_north", BlockStateProperties.NORTH_REDSTONE);
		Registry.register(PROPERTIES, "redstone_south", BlockStateProperties.SOUTH_REDSTONE);
		Registry.register(PROPERTIES, "redstone_west", BlockStateProperties.WEST_REDSTONE);
		Registry.register(PROPERTIES, "double_block_half", BlockStateProperties.DOUBLE_BLOCK_HALF);
		Registry.register(PROPERTIES, "half", BlockStateProperties.HALF);
		Registry.register(PROPERTIES, "rail_shape", BlockStateProperties.RAIL_SHAPE);
		Registry.register(PROPERTIES, "rail_shape_straight", BlockStateProperties.RAIL_SHAPE_STRAIGHT);
		Registry.register(PROPERTIES, "age_0_1", BlockStateProperties.AGE_1);
		Registry.register(PROPERTIES, "age_0_2", BlockStateProperties.AGE_2);
		Registry.register(PROPERTIES, "age_0_3", BlockStateProperties.AGE_3);
		Registry.register(PROPERTIES, "age_0_4", BlockStateProperties.AGE_4);
		Registry.register(PROPERTIES, "age_0_5", BlockStateProperties.AGE_5);
		Registry.register(PROPERTIES, "age_0_7", BlockStateProperties.AGE_7);
		Registry.register(PROPERTIES, "age_0_15", BlockStateProperties.AGE_15);
		Registry.register(PROPERTIES, "age_0_25", BlockStateProperties.AGE_25);
		Registry.register(PROPERTIES, "bites_0_6", BlockStateProperties.BITES);
		Registry.register(PROPERTIES, "candles", BlockStateProperties.CANDLES);
		Registry.register(PROPERTIES, "delay_1_4", BlockStateProperties.DELAY);
		Registry.register(PROPERTIES, "distance_1_7", BlockStateProperties.DISTANCE);
		Registry.register(PROPERTIES, "eggs_1_4", BlockStateProperties.EGGS);
		Registry.register(PROPERTIES, "hatch_0_2", BlockStateProperties.HATCH);
		Registry.register(PROPERTIES, "layers_1_8", BlockStateProperties.LAYERS);
		Registry.register(PROPERTIES, "level_0_3", BlockStateProperties.LEVEL_CAULDRON);
		Registry.register(PROPERTIES, "level_0_8", BlockStateProperties.LEVEL_COMPOSTER);
		Registry.register(PROPERTIES, "level_1_8", BlockStateProperties.LEVEL_FLOWING);
		Registry.register(PROPERTIES, "honey_level", BlockStateProperties.LEVEL_HONEY);
		Registry.register(PROPERTIES, "level_0_15", BlockStateProperties.LEVEL);
		Registry.register(PROPERTIES, "moisture_0_7", BlockStateProperties.MOISTURE);
		Registry.register(PROPERTIES, "note_0_24", BlockStateProperties.NOTE);
		Registry.register(PROPERTIES, "pickles_1_4", BlockStateProperties.PICKLES);
		Registry.register(PROPERTIES, "power_0_15", BlockStateProperties.POWER);
		Registry.register(PROPERTIES, "stage_0_1", BlockStateProperties.STAGE);
		Registry.register(PROPERTIES, "distance_0_7", BlockStateProperties.STABILITY_DISTANCE);
		Registry.register(PROPERTIES, "charges", BlockStateProperties.RESPAWN_ANCHOR_CHARGES);
		Registry.register(PROPERTIES, "rotation_0_15", BlockStateProperties.ROTATION_16);
		Registry.register(PROPERTIES, "bed_part", BlockStateProperties.BED_PART);
		Registry.register(PROPERTIES, "chest_type", BlockStateProperties.CHEST_TYPE);
		Registry.register(PROPERTIES, "comparator_mode", BlockStateProperties.MODE_COMPARATOR);
		Registry.register(PROPERTIES, "door_hinge", BlockStateProperties.DOOR_HINGE);
		Registry.register(PROPERTIES, "note_block_instrument", BlockStateProperties.NOTEBLOCK_INSTRUMENT);
		Registry.register(PROPERTIES, "piston_type", BlockStateProperties.PISTON_TYPE);
		Registry.register(PROPERTIES, "slab_type", BlockStateProperties.SLAB_TYPE);
		Registry.register(PROPERTIES, "stairs_shape", BlockStateProperties.STAIRS_SHAPE);
		Registry.register(PROPERTIES, "structure_block_mode", BlockStateProperties.STRUCTUREBLOCK_MODE);
		Registry.register(PROPERTIES, "bamboo_leaves", BlockStateProperties.BAMBOO_LEAVES);
		Registry.register(PROPERTIES, "tilt", BlockStateProperties.TILT);
		Registry.register(PROPERTIES, "vertical_direction", BlockStateProperties.VERTICAL_DIRECTION);
		Registry.register(PROPERTIES, "speleothem_thickness", BlockStateProperties.SPELEOTHEM_THICKNESS);
		Registry.register(PROPERTIES, "sculk_sensor_phase", BlockStateProperties.SCULK_SENSOR_PHASE);
		Registry.register(PROPERTIES, "chiseled_bookshelf_slot_0_occupied", BlockStateProperties.SLOT_0_OCCUPIED);
		Registry.register(PROPERTIES, "chiseled_bookshelf_slot_1_occupied", BlockStateProperties.SLOT_1_OCCUPIED);
		Registry.register(PROPERTIES, "chiseled_bookshelf_slot_2_occupied", BlockStateProperties.SLOT_2_OCCUPIED);
		Registry.register(PROPERTIES, "chiseled_bookshelf_slot_3_occupied", BlockStateProperties.SLOT_3_OCCUPIED);
		Registry.register(PROPERTIES, "chiseled_bookshelf_slot_4_occupied", BlockStateProperties.SLOT_4_OCCUPIED);
		Registry.register(PROPERTIES, "chiseled_bookshelf_slot_5_occupied", BlockStateProperties.SLOT_5_OCCUPIED);
		Registry.register(PROPERTIES, "dusted", BlockStateProperties.DUSTED);
		Registry.register(PROPERTIES, "cracked", BlockStateProperties.CRACKED);
		Registry.register(PROPERTIES, "crafting", BlockStateProperties.CRAFTING);
		Registry.register(PROPERTIES, "trial_spawner_state", BlockStateProperties.TRIAL_SPAWNER_STATE);
	}

	private static void registerEntityComponents() {
		registerInRegistryVanilla(ENTITY_COMPONENTS, "annotation.break_door", BreakDoorAnnotationComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENTS, "annotation.open_door", OpenDoorAnnotationComponent.class);

		registerInRegistryVanilla(ENTITY_COMPONENTS, "admire_item", AdmireItemComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENTS, "ageable", AgeableComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENTS, "angry", AngryComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENTS, "area_attack", AreaAttackComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENTS, "attack_cooldown", AttackCooldownComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENTS, "barter", BarterComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENTS, "block_sensor", BlockSensorComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENTS, "boostable", BoostableComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENTS, "boss", BossComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENTS, "break_blocks", BreakBlocksComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENTS, "breathable", BreathableComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENTS, "celebrate", CelebrateBehaviourComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENTS, "collision_box", CollisionBoxComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENTS, "health", HealthComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENTS, "movement", MovementComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENTS, "nameable", NameableComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENTS, "ai", io.github.vampirestudios.obsidian.api.obsidian.entity.Entity.StructuredAI.class);

		registerInRegistryVanilla(ENTITY_COMPONENTS, "movement.basic", BasicMovementComponent.class);

		registerInRegistryVanilla(ENTITY_COMPONENTS, "behavior.panic", PanicBehaviourComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENTS, "behavior.tempt", TemptBehaviourComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENTS, "behavior.random_stroll", RandomStrollBehaviourComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENTS, "behavior.random_look_around", RandomLookAroundBehaviourComponent.class);
		registerInRegistryVanilla(ENTITY_COMPONENTS, "behavior.look_at_player", LookAtPlayerBehaviourComponent.class);
	}

	private static void registerBedrockBlockEvent() {
//		registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "add_mob_effect", AddMobEffect.class);
//		registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "damage", Damage.class);
//		registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "decrement_stack", DecrementStack.class);
//		registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "die", Die.class);
//		registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "play_effect", PlayEffect.class);
////        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "play_sound", LookAtPlayerBehaviourComponent.class);
////        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "remove_mob_effect", LookAtPlayerBehaviourComponent.class);
////        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "run_command", LookAtPlayerBehaviourComponent.class);
////        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "set_block", LookAtPlayerBehaviourComponent.class);
////        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "set_block_at_pos", LookAtPlayerBehaviourComponent.class);
////        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "set_block_property", LookAtPlayerBehaviourComponent.class);
////        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "spawn_loot", LookAtPlayerBehaviourComponent.class);
////        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "swing", LookAtPlayerBehaviourComponent.class);
////        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "teleport", LookAtPlayerBehaviourComponent.class);
////        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "transform_item", LookAtPlayerBehaviourComponent.class);
	}

	private static void registerObsidianBlockEvent() {
//		registerInRegistryVanilla(BLOCK_EVENT_REGISTRY, "add_mob_effect", io.github.vampirestudios.obsidian.api.obsidian.block.events.AddMobEffect.class);
//		registerInRegistryVanilla(BLOCK_EVENT_REGISTRY, "damage", io.github.vampirestudios.obsidian.api.obsidian.block.events.Damage.class);
//		registerInRegistryVanilla(BLOCK_EVENT_REGISTRY, "decrement_stack", io.github.vampirestudios.obsidian.api.obsidian.block.events.DecrementStack.class);
//		registerInRegistryVanilla(BLOCK_EVENT_REGISTRY, "die", io.github.vampirestudios.obsidian.api.obsidian.block.events.Die.class);
//		registerInRegistryVanilla(BLOCK_EVENT_REGISTRY, "play_effect", io.github.vampirestudios.obsidian.api.obsidian.block.events.PlayEffect.class);
////        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "play_sound", LookAtPlayerBehaviourComponent.class);
////        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "remove_mob_effect", LookAtPlayerBehaviourComponent.class);
////        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "run_command", LookAtPlayerBehaviourComponent.class);
////        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "set_block", LookAtPlayerBehaviourComponent.class);
////        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "set_block_at_pos", LookAtPlayerBehaviourComponent.class);
////        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "set_block_property", LookAtPlayerBehaviourComponent.class);
////        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "spawn_loot", LookAtPlayerBehaviourComponent.class);
////        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "swing", LookAtPlayerBehaviourComponent.class);
////        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "teleport", LookAtPlayerBehaviourComponent.class);
////        registerInRegistryVanilla(BEDROCK_BLOCK_EVENT_REGISTRY, "transform_item", LookAtPlayerBehaviourComponent.class);
	}

	private static void registerFoods() {
		registerInRegistryVanilla(FOODS, "apple", Foods.APPLE);
		registerInRegistryVanilla(FOODS, "baked_potato", Foods.BAKED_POTATO);
		registerInRegistryVanilla(FOODS, "beef", Foods.BEEF);
		registerInRegistryVanilla(FOODS, "beetroot", Foods.BEETROOT);
		registerInRegistryVanilla(FOODS, "beetroot_soup", Foods.BEETROOT_SOUP);
		registerInRegistryVanilla(FOODS, "bread", Foods.BREAD);
		registerInRegistryVanilla(FOODS, "carrot", Foods.CARROT);
		registerInRegistryVanilla(FOODS, "chicken", Foods.CHICKEN);
		registerInRegistryVanilla(FOODS, "chorus_fruit", Foods.CHORUS_FRUIT);
		registerInRegistryVanilla(FOODS, "cod", Foods.COD);
		registerInRegistryVanilla(FOODS, "cooked_beef", Foods.COOKED_BEEF);
		registerInRegistryVanilla(FOODS, "cooked_chicken", Foods.COOKED_CHICKEN);
		registerInRegistryVanilla(FOODS, "cooked_cod", Foods.COOKED_COD);
		registerInRegistryVanilla(FOODS, "cooked_mutton", Foods.COOKED_MUTTON);
		registerInRegistryVanilla(FOODS, "cooked_porkchop", Foods.COOKED_PORKCHOP);
		registerInRegistryVanilla(FOODS, "cooked_rabbit", Foods.COOKED_RABBIT);
		registerInRegistryVanilla(FOODS, "cooked_salmon", Foods.COOKED_SALMON);
		registerInRegistryVanilla(FOODS, "cookie", Foods.COOKIE);
		registerInRegistryVanilla(FOODS, "dried_kelp", Foods.DRIED_KELP);
		registerInRegistryVanilla(FOODS, "enchanted_golden_apple", Foods.ENCHANTED_GOLDEN_APPLE);
		registerInRegistryVanilla(FOODS, "golden_apple", Foods.GOLDEN_APPLE);
		registerInRegistryVanilla(FOODS, "golden_carrot", Foods.GOLDEN_CARROT);
		registerInRegistryVanilla(FOODS, "honey_bottle", Foods.HONEY_BOTTLE);
		registerInRegistryVanilla(FOODS, "melon_slice", Foods.MELON_SLICE);
		registerInRegistryVanilla(FOODS, "mushroom_stew", Foods.MUSHROOM_STEW);
		registerInRegistryVanilla(FOODS, "mutton", Foods.MUTTON);
		registerInRegistryVanilla(FOODS, "poisonous_potato", Foods.POISONOUS_POTATO);
		registerInRegistryVanilla(FOODS, "porkchop", Foods.PORKCHOP);
		registerInRegistryVanilla(FOODS, "potato", Foods.POTATO);
		registerInRegistryVanilla(FOODS, "pufferfish", Foods.PUFFERFISH);
		registerInRegistryVanilla(FOODS, "pumpkin_pie", Foods.PUMPKIN_PIE);
		registerInRegistryVanilla(FOODS, "rabbit", Foods.RABBIT);
		registerInRegistryVanilla(FOODS, "rabbit_stew", Foods.RABBIT_STEW);
		registerInRegistryVanilla(FOODS, "rotten_flesh", Foods.ROTTEN_FLESH);
		registerInRegistryVanilla(FOODS, "salmon", Foods.SALMON);
		registerInRegistryVanilla(FOODS, "spider_eye", Foods.SPIDER_EYE);
		registerInRegistryVanilla(FOODS, "suspicious_stew", Foods.SUSPICIOUS_STEW);
		registerInRegistryVanilla(FOODS, "sweet_berries", Foods.SWEET_BERRIES);
		registerInRegistryVanilla(FOODS, "glow_berries", Foods.GLOW_BERRIES);
		registerInRegistryVanilla(FOODS, "tropical_fish", Foods.TROPICAL_FISH);
	}

}
