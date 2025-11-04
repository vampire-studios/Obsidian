package io.github.vampirestudios.obsidian.registry;

import io.github.vampirestudios.obsidian.Obsidian;
import io.github.vampirestudios.obsidian.minecraft.*;
import io.github.vampirestudios.obsidian.registry.components.Wearable;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

import java.util.function.UnaryOperator;

public class OItemComponents {

	public static final DataComponentType<ResourceLocation> CREATIVE_TAB = register("creative_tab", builder -> builder
			.persistent(ResourceLocation.CODEC)
			.networkSynchronized(ResourceLocation.STREAM_CODEC)
	);
	public static final DataComponentType<Wearable> WEARABLE = register("wearable", builder -> builder
			.persistent(Wearable.CODEC)
			.networkSynchronized(Wearable.STREAM_CODEC)
	);
	public static final DataComponentType<Integer> MINING_RADIUS = register("mining_radius", builder -> builder
			.persistent(ExtraCodecs.POSITIVE_INT)
			.networkSynchronized(ByteBufCodecs.VAR_INT)
	);
	public static final DataComponentType<Vector3f> MINING_AREA = register("mining_area", builder -> builder
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
			"bundle_contents", builder -> builder.persistent(BundleContents.CODEC).networkSynchronized(BundleContents.STREAM_CODEC).cacheEncoding()
	);
	public static final DataComponentType<BundleBarColors> BUNDLE_BAR_COLORS = register(
			"bundle_bar_colors", builder -> builder.persistent(BundleBarColors.CODEC).networkSynchronized(BundleBarColors.STREAM_CODEC).cacheEncoding()
	);
//	public static final DataComponentType<CustomMenuComponent> CUSTOM_MENU = register(
//			"custom_menu", builder -> builder.persistent(CustomMenuComponent.CODEC).networkSynchronized(CustomMenuComponent.STREAM_CODEC)
//	);

	public static void init() {}

	private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
		return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Obsidian.id(name), builder.apply(DataComponentType.builder()).build());
	}
}
