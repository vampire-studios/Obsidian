package io.github.vampirestudios.obsidian.registry;

import com.mojang.serialization.Lifecycle;
import io.github.vampirestudios.obsidian.Const;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.DynamicShape;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.Transformation;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.state.property.Property;

public class Registries {
	public static final Registry<AddonModule> ADDON_MODULE_REGISTRY;
	public static final Registry<ItemGroup> ITEM_GROUP_REGISTRY;
	public static final Registry<FoodComponent> FOOD_COMPONENTS;
	public static final Registry<Class<? extends Component>> ENTITY_COMPONENT_REGISTRY;
	public static final Registry<Property> PROPERTIES;
	public static final Registry<DynamicShape> DYNAMIC_SHAPES;
	public static Registry<Animation> ANIMATION_DEFINITIONS;
	public static Registry<Transformation.Interpolation> ANIMATION_CHANNEL_INTERPOLATIONS;
	public static Registry<Transformation.Target> ANIMATION_CHANNEL_TARGETS;

	static {
		ADDON_MODULE_REGISTRY = FabricRegistryBuilder.createSimple(AddonModule.class, Const.id("addon_modules")).buildAndRegister();
		ITEM_GROUP_REGISTRY = FabricRegistryBuilder.createSimple(ItemGroup.class, Const.id("item_groups")).buildAndRegister();
		FOOD_COMPONENTS = FabricRegistryBuilder.createSimple(FoodComponent.class, Const.id("food_components")).buildAndRegister();
		ENTITY_COMPONENT_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry( Const.id("entity_components")), Lifecycle.stable(), false);
		PROPERTIES = FabricRegistryBuilder.createSimple(Property.class, Const.id("properties")).buildAndRegister();
		DYNAMIC_SHAPES = FabricRegistryBuilder.createSimple(DynamicShape.class, Const.id("dynamic_shapes")).buildAndRegister();
		ANIMATION_DEFINITIONS = FabricRegistryBuilder.createSimple(Animation.class, Const.id("animation_definitions")).buildAndRegister();
		ANIMATION_CHANNEL_INTERPOLATIONS = FabricRegistryBuilder.createSimple(Transformation.Interpolation.class, Const.id("animation_channel_interpolations")).buildAndRegister();
		ANIMATION_CHANNEL_TARGETS = FabricRegistryBuilder.createSimple(Transformation.Target.class, Const.id("animation_channel_targets")).buildAndRegister();
	}
}