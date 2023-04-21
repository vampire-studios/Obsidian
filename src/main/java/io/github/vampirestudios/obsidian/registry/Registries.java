package io.github.vampirestudios.obsidian.registry;

import com.mojang.serialization.Lifecycle;
import io.github.vampirestudios.obsidian.Const;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.DynamicShape;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.state.properties.Property;

public class Registries {
	public static final Registry<AddonModule> ADDON_MODULE_REGISTRY;
	public static final Registry<CreativeModeTab> ITEM_GROUP_REGISTRY;
	public static final Registry<FoodProperties> FOOD_COMPONENTS;
	public static final Registry<Class<? extends Component>> ENTITY_COMPONENT_REGISTRY;
	public static final Registry<Property> PROPERTIES;
	public static final Registry<DynamicShape> DYNAMIC_SHAPES;
//	public static final Registry<Transformation.Target> BLOCK_PROPERTIES;
//	public static final Registry<Transformation.Target> BLOCK_GROUPS;
	public static Registry<AnimationDefinition> ANIMATION_DEFINITIONS;
	public static Registry<AnimationChannel.Interpolation> ANIMATION_CHANNEL_INTERPOLATIONS;
	public static Registry<AnimationChannel.Target> ANIMATION_CHANNEL_TARGETS;

	static {
		ADDON_MODULE_REGISTRY = FabricRegistryBuilder.createSimple(AddonModule.class, Const.id("addon_modules")).buildAndRegister();
		ITEM_GROUP_REGISTRY = FabricRegistryBuilder.createSimple(CreativeModeTab.class, Const.id("item_groups")).buildAndRegister();
		FOOD_COMPONENTS = FabricRegistryBuilder.createSimple(FoodProperties.class, Const.id("food_components")).buildAndRegister();
		ENTITY_COMPONENT_REGISTRY = new MappedRegistry<>(ResourceKey.createRegistryKey( Const.id("entity_components")), Lifecycle.stable(), false);
		PROPERTIES = FabricRegistryBuilder.createSimple(Property.class, Const.id("properties")).buildAndRegister();
		DYNAMIC_SHAPES = FabricRegistryBuilder.createSimple(DynamicShape.class, Const.id("dynamic_shapes")).buildAndRegister();
//		BLOCK_PROPERTIES = FabricRegistryBuilder.createSimple(DynamicShape.class, Const.id("dynamic_shapes")).buildAndRegister();
		ANIMATION_DEFINITIONS = FabricRegistryBuilder.createSimple(AnimationDefinition.class, Const.id("animation_definitions")).buildAndRegister();
		ANIMATION_CHANNEL_INTERPOLATIONS = FabricRegistryBuilder.createSimple(AnimationChannel.Interpolation.class, Const.id("animation_channel_interpolations")).buildAndRegister();
		ANIMATION_CHANNEL_TARGETS = FabricRegistryBuilder.createSimple(AnimationChannel.Target.class, Const.id("animation_channel_targets")).buildAndRegister();
	}
}