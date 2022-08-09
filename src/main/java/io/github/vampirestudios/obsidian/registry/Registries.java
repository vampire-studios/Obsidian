package io.github.vampirestudios.obsidian.registry;

import com.mojang.serialization.Lifecycle;
import io.github.vampirestudios.obsidian.Const;
import io.github.vampirestudios.obsidian.api.obsidian.AddonModule;
import io.github.vampirestudios.obsidian.api.obsidian.DynamicShape;
import io.github.vampirestudios.obsidian.api.obsidian.entity.Component;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.client.render.animation.Animation;
import net.minecraft.client.render.animation.PartAnimation;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.property.Property;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.registry.SimpleRegistry;

public class Registries {
	public static final Registry<AddonModule> ADDON_MODULE_REGISTRY;
	public static final Registry<ItemGroup> ITEM_GROUP_REGISTRY;
	public static final Registry<FoodComponent> FOOD_COMPONENTS;
	public static final Registry<Class<? extends Component>> ENTITY_COMPONENT_REGISTRY;
	public static final Registry<Property> PROPERTIES;
	public static final Registry<DynamicShape> DYNAMIC_SHAPES;
	public static Registry<Animation> ANIMATION_DEFINITIONS;
	public static Registry<PartAnimation.Interpolator> ANIMATION_CHANNEL_INTERPOLATIONS;
	public static Registry<PartAnimation.AnimationTargets> ANIMATION_CHANNEL_TARGETS;

	static {
		ADDON_MODULE_REGISTRY = FabricRegistryBuilder.createSimple(AddonModule.class, Const.id("addon_modules")).buildAndRegister();
		ITEM_GROUP_REGISTRY = FabricRegistryBuilder.createSimple(ItemGroup.class, Const.id("item_groups")).buildAndRegister();
		FOOD_COMPONENTS = FabricRegistryBuilder.createSimple(FoodComponent.class, Const.id("food_components")).buildAndRegister();
		ENTITY_COMPONENT_REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry( Const.id("entity_components")), Lifecycle.stable(), null);
		PROPERTIES = FabricRegistryBuilder.createSimple(Property.class, Const.id("properties")).buildAndRegister();
		DYNAMIC_SHAPES = FabricRegistryBuilder.createSimple(DynamicShape.class, Const.id("dynamic_shapes")).buildAndRegister();
		ANIMATION_DEFINITIONS = FabricRegistryBuilder.createSimple(Animation.class, Const.id("animation_definitions")).buildAndRegister();
		ANIMATION_CHANNEL_INTERPOLATIONS = FabricRegistryBuilder.createSimple(PartAnimation.Interpolator.class, Const.id("animation_channel_interpolations")).buildAndRegister();
		ANIMATION_CHANNEL_TARGETS = FabricRegistryBuilder.createSimple(PartAnimation.AnimationTargets.class, Const.id("animation_channel_targets")).buildAndRegister();
	}
}