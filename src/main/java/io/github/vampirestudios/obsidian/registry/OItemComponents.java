package io.github.vampirestudios.obsidian.registry;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.minecraft.*;
import io.github.vampirestudios.obsidian.registry.components.EnergyStorage;
import io.github.vampirestudios.obsidian.registry.components.FluidContents;
import io.github.vampirestudios.obsidian.registry.components.Attraction;
import io.github.vampirestudios.obsidian.registry.components.Wearable;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3fc;

import java.util.function.UnaryOperator;

public class OItemComponents {

	public static final DataComponentType<Identifier> CREATIVE_TAB = register("creative_tab", builder -> builder
			.persistent(Identifier.CODEC)
			.networkSynchronized(Identifier.STREAM_CODEC)
	);
	public static final DataComponentType<Wearable> WEARABLE = register("wearable", builder -> builder
			.persistent(Wearable.CODEC)
			.networkSynchronized(Wearable.STREAM_CODEC)
	);
	public static final DataComponentType<Integer> MINING_RADIUS = register("mining_radius", builder -> builder
			.persistent(ExtraCodecs.POSITIVE_INT)
			.networkSynchronized(ByteBufCodecs.VAR_INT)
	);
	public static final DataComponentType<Vector3fc> MINING_AREA = register("mining_area", builder -> builder
			.persistent(ExtraCodecs.VECTOR3F)
			.networkSynchronized(ByteBufCodecs.VECTOR3F)
	);
	public static final DataComponentType<StorageItemComponent> STORAGE_ITEM = register("storage_item", builder -> builder
			.persistent(StorageItemComponent.CODEC)
			.networkSynchronized(StorageItemComponent.STREAM_CODEC)
	);
	public static final DataComponentType<BundleInteraction> BUNDLE_INTERACTION = register("bundle_interaction", builder -> builder
			.persistent(BundleInteraction.CODEC)
			.networkSynchronized(BundleInteraction.STREAM_CODEC)
	);
	public static final DataComponentType<BundleContents> BUNDLE_CONTENTS = register(
			"bundle_contents", builder -> builder.persistent(BundleContents.CODEC).networkSynchronized(BundleContents.STREAM_CODEC)
	);
	public static final DataComponentType<BundleBarColors> BUNDLE_BAR_COLORS = register(
			"bundle_bar_colors", builder -> builder.persistent(BundleBarColors.CODEC).networkSynchronized(BundleBarColors.STREAM_CODEC)
	);
	public static final DataComponentType<ScopeComponent> SCOPE = register(
			"scope", builder -> builder.persistent(ScopeComponent.CODEC).networkSynchronized(ScopeComponent.STREAM_CODEC)
	);
	public static final DataComponentType<TridentComponent> TRIDENT = register("trident", builder -> builder
			.persistent(TridentComponent.CODEC)
			.networkSynchronized(TridentComponent.STREAM_CODEC)
	);
	public static final DataComponentType<ThrowableComponent> THROWABLE = register(
			"throwable", builder -> builder.persistent(ThrowableComponent.CODEC).networkSynchronized(ThrowableComponent.STREAM_CODEC)
	);
	public static final DataComponentType<ShooterComponent> SHOOTER = register(
			"shooter", builder -> builder.persistent(ShooterComponent.CODEC).networkSynchronized(ShooterComponent.STREAM_CODEC)
	);
	public static final DataComponentType<ChargeComponent> CHARGE = register(
			"charge", builder -> builder.persistent(ChargeComponent.CODEC).networkSynchronized(ChargeComponent.STREAM_CODEC)
	);
//	public static final DataComponentType<CustomMenuComponent> CUSTOM_MENU = register(
//			"custom_menu", builder -> builder.persistent(CustomMenuComponent.CODEC).networkSynchronized(CustomMenuComponent.STREAM_CODEC)
//	);
	public static final DataComponentType<FluidContents> FLUID_CONTENTS =
		register("fluid_contents", builder -> builder.persistent(FluidContents.CODEC));

	public static final DataComponentType<EnergyStorage> ENERGY =
			register("energy", builder -> builder.persistent(EnergyStorage.CODEC));

	public static final DataComponentType<Attraction> MAGNET =
			register("magnet", builder -> builder.persistent(Attraction.CODEC));


	public static void init() {}

	private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
		return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Obsidian.id(name), builder.apply(DataComponentType.builder()).build());
	}
}
